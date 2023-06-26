@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ithoughts.mynaa.tsd.R
import com.ithoughts.mynaa.tsd.rss.DateParser
import com.ithoughts.mynaa.tsd.rss.ParsingState
import com.ithoughts.mynaa.tsd.rss.RssViewModal
import com.ithoughts.mynaa.tsd.rss.db.ArticleItem
import com.ithoughts.mynaa.tsd.rss.db.FeedArticle


@Composable
fun RssScreen(feedId: Long) {
    val context = LocalContext.current
    val viewModal = viewModel(initializer = {
        RssViewModal(feedId, (context as Activity).application)
    })
    val parsingState by viewModal.parsingState.collectAsState()
    val feedArticles by viewModal.feedArticles.collectAsState(initial = null)

    val hostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
            TopAppBar(
                title = { Text("Feed reader") }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            feedArticles?.let { RssItemsColumn(it) }
            AnimatedVisibility(parsingState == ParsingState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }

    LaunchedEffect(parsingState) {
        when (parsingState) {
            is ParsingState.Error -> {
                RssViewModal.info((parsingState as ParsingState.Error).message)
                hostState.showSnackbar(
                    (parsingState as ParsingState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> {}
        }
    }
}

@Composable
fun RssItemsColumn(feedArticle: FeedArticle) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            feedArticle.feed.also { feed ->
                Text(text = feed.title, style = MaterialTheme.typography.labelMedium)
                feed.lastBuildDate?.let { Text(text = DateParser.format(it)) }
            }
        }
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(12.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                feedArticle.articles,
                key = { it.id }
            ) {
                RssItemCard(it)
            }
        }
    }
}

@Composable
fun RssItemCard(item: ArticleItem) {
    var imageSrc by remember { mutableStateOf<String?>(null) }
    val linkColor = MaterialTheme.colorScheme.secondary.toArgb()
    val context = LocalContext.current
    ElevatedCard(
        onClick = {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, Uri.parse(item.link))
        },
        shape = RoundedCornerShape(16.dp),
    ) {
        imageSrc?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageSrc)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.baseline_photo_size_select_actual_24),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                filterQuality = FilterQuality.Medium
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.headlineSmall, fontSize = 19.sp, lineHeight = 30.sp)
            Spacer(modifier = Modifier.height(12.dp))
            item.description?.let { description ->
                AndroidView(factory = {
                    TextView(it).apply {
                        linksClickable = true
                        setLinkTextColor(linkColor)
                        textSize = 16f
                    }
                }) { textView ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        textView.text = Html.fromHtml(
                            description,
                            Html.FROM_HTML_MODE_LEGACY or Html.FROM_HTML_OPTION_USE_CSS_COLORS,
                            {
                                imageSrc = it
                                ColorDrawable(0xfffffff)
                            },
                            { _, _, _, _ -> }
                        )
                    } else Html.fromHtml(description)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
            ) {
                item.pubDate?.let {
                    Text(
                        text = DateParser.format(it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(text = item.category, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}