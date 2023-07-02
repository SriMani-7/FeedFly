@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.ithoughts.mynaa.tsd.rss.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html.ImageGetter
import android.text.SpannableStringBuilder
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ithoughts.mynaa.tsd.R
import com.ithoughts.mynaa.tsd.rss.DateParser
import com.ithoughts.mynaa.tsd.rss.ParsingState
import com.ithoughts.mynaa.tsd.rss.db.ArticleItem
import com.ithoughts.mynaa.tsd.rss.fromHtml
import com.ithoughts.mynaa.tsd.rss.toAnnotatedString
import com.ithoughts.mynaa.tsd.rss.vm.RssViewModal


@Composable
fun RssScreen(feedId: Long, navController: NavHostController) {
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
                navigationIcon = { BackButton(navController) },
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
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(14.dp, 5.dp),
                                text = date,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
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
    val context = LocalContext.current
    var showImage by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = {
            val intent = CustomTabsIntent.Builder().build().apply {
                intent.putExtra("com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB", true)
            }
            intent.launchUrl(context, Uri.parse(item.link))
        },
        shape = MaterialTheme.shapes.large
    ) {
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
                        .clickable { showImage = true }
                        .defaultMinSize(minHeight = 180.dp),
                    filterQuality = FilterQuality.Medium,
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .then(
                        if (imageSrc == null) Modifier.padding(
                            12.dp,
                            top = 16.dp,
                            bottom = 10.dp,
                            end = 12.dp
                        )
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
                .padding(12.dp, bottom = 14.dp, top = 5.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item.description?.let {
                DescriptionText(it) { src ->
                    imageSrc = src
                    RssViewModal.info(src)
                    ColorDrawable(android.graphics.Color.GRAY)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.category.isNotBlank())
                    Text(text = item.category, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.weight(1f))
                DateParser.formatTime(item.pubDate)
                    ?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
    if (showImage && imageSrc != null)
        ShowImageDialog(imageSrc!!) {
            showImage = false
        }
}

@Composable
fun ShowImageDialog(
    imageSrc: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, _ ->
            scale *= zoomChange
            offset += offsetChange
        }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageSrc)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.baseline_photo_size_select_actual_24),
                contentDescription = "image",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(transformableState),
                filterQuality = FilterQuality.High,
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Close, "close")
            }
        }
    }
    LaunchedEffect(scale) {
        if (scale < 0.7f) onDismiss()
    }
}

@Composable
fun DescriptionText(
    description: String,
    imageGetter: ImageGetter,
) {
    var spanned by remember { mutableStateOf<AnnotatedString?>(null) }
    spanned?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light,
        )
    }
    LaunchedEffect(Unit) {
        spanned = (fromHtml(description, imageGetter) as SpannableStringBuilder).toAnnotatedString()
    }
}