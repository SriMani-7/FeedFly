@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.audio.MediaViewModel
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem
import java.text.SimpleDateFormat

@Composable
fun RssItemsColumn(
    channelList: List<ChannelItem>,
    viewModel: MediaViewModel = viewModel()
) {
    val lazyListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(8.dp, 15.dp),
            state = lazyListState
        ) {
            items(channelList, key = { it.link ?: "null" }) {
                RssItemCard(
                    it,
                    modifier = Modifier.animateItemPlacement(),
                    onPlayAudio = viewModel::play
                )
            }
        }
        AnimatedVisibility(
            viewModel.songState != null,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            viewModel.songState?.let {
                ExoPlayerCard(
                    songState = it,
                    audioMetaData = viewModel.audioMetaData,
                    playToggle = viewModel::play
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.mExoPlayer.release()
        }
    }
}

@Composable
fun RssItemCard(
    item: ChannelItem,
    modifier: Modifier,
    onPlayAudio: (String) -> Unit
) {
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val pubTime by remember {
        mutableStateOf(
            DateParser.parseDate(item.pubDate)
                ?.let { SimpleDateFormat.getDateTimeInstance().format(it) }
                ?: ""
        )
    }
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    OutlinedCard(
        onClick = { scope.launch { articleModalState.show() } },
        shape = MaterialTheme.shapes.medium,
        border = CardDefaults.outlinedCardBorder().copy(.4.dp),
        modifier = modifier
    ) {
        item.description?.let {
            HtmlImage(it) { src ->
                descriptionUri = src
                null
            }
            if (descriptionUri != null && ArticleMedia.isImage(item.enclosure?.type))
                ArticleImage(descriptionUri!!)
        }
        item.enclosure?.let {
            ArticleMediaHeader(ArticleMedia(it, 1), onPlayAudio)
        }
        Column(
            modifier = Modifier
                .padding(14.dp, 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.title?.isNotBlank() == true) {
                Text(
                    text = item.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = item.categories.joinToString(","),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pubTime,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        }
    }
    if (articleModalState.isVisible) {
        ArticleViewScreen(item.description, item.link ?: "", articleModalState) {
            scope.launch { articleModalState.hide() }
        }
    }
}

@Composable
fun ArticleViewScreen(
    description: String?,
    link: String,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomAppBar(
                actions = {
                    listOf(
                        "Share" to R.drawable.share_24px,
                        "browser" to R.drawable.open_in_browser_24px
                    ).forEach {
                        IconButton(onClick = {
                            when (it.first) {
                                "browser" -> openInBrowser(link, context)
                                "Share" -> shareText(link, context)
                            }
                        }) {
                            Icon(painterResource(it.second), it.first)
                        }
                    }
                }, floatingActionButton = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
            )
        }
    ) {
        Divider()
        if (description != null)
            AndroidView(
                factory = {
                    DescriptionWebView(it, description)
                },
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) else {
            Text(
                text = "No description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 20.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
