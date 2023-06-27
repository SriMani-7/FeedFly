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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ithoughts.mynaa.tsd.R
import com.ithoughts.mynaa.tsd.rss.DateParser
import com.ithoughts.mynaa.tsd.rss.ParsingState
import com.ithoughts.mynaa.tsd.rss.RssViewModal
import com.ithoughts.mynaa.tsd.rss.db.ArticleItem


@Composable
fun RssScreen(feedId: Long) {
    val context = LocalContext.current
    val viewModal = viewModel(initializer = {
        RssViewModal(feedId, (context as Activity).application)
    })
    val parsingState by viewModal.parsingState.collectAsState()
    val feedArticles by viewModal.groupedArticles.collectAsState(initial = null)
    val feed by viewModal.feed.collectAsState(initial = null)

    val hostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.ArrowBack, "back")
                    }
                },
                title = {
                    Column {
                        feed.also { feed ->
                            feed?.title?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DateParser.formatDate(feed?.lastBuildDate)?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
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
fun RssItemsColumn(dateListMap: Map<String?, List<ArticleItem>>) {
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(10.dp, 15.dp)
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp, 8.dp),
                                text = date,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                items(entry.value,
                    key = { it.id }
                ) {
                    RssItemCard(it)
                }
            }
        }
    }
}

@Composable
fun RssItemCard(item: ArticleItem) {
    var imageSrc by remember { mutableStateOf<String?>(null) }
    val linkColor = MaterialTheme.colorScheme.secondary.toArgb()
    val context = LocalContext.current
    Surface(
        onClick = {
            val intent = CustomTabsIntent.Builder().build().apply {
                intent.putExtra("com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB", true)
            }
            intent.launchUrl(context, Uri.parse(item.link))
        },
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box {
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
                            .defaultMinSize(minHeight = 180.dp),
                        filterQuality = FilterQuality.Medium,
                    )
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .then(
                            if (imageSrc == null) Modifier.padding(12.dp, 10.dp)
                            else Modifier
                                .background(
                                    MaterialTheme.colorScheme
                                        .surfaceColorAtElevation(6.dp)
                                        .copy(alpha = 0.8f)
                                )
                                .padding(12.dp, 12.dp)

                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, bottom = 14.dp, top = 3.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.description?.let { description ->
                    AndroidView(factory = {
                        TextView(it).apply {
                            linksClickable = true
                            setLinkTextColor(linkColor)
                            textSize = 14.5f
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
                if (item.category.isNotBlank())
                    Text(text = item.category, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}