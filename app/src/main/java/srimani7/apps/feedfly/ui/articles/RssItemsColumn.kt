package srimani7.apps.feedfly.ui.articles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import srimani7.apps.feedfly.audio.MediaViewModel
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.ui.ExoPlayerCard
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RssItemsColumn(
    dateListMap: Map<String?, List<LabelledArticle>>,
    viewModel: MediaViewModel = viewModel(),
    onDeleteArticle: (Long) -> Unit,
    onMoveToPrivate: (Long) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(8.dp, 16.dp),
            state = lazyListState
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.articleId },
                    contentType = { it.mediaType }
                ) { feedArticle ->
                    val currentItem by rememberUpdatedState(feedArticle.articleId)
                    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
                        when (it) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                onDeleteArticle(currentItem)
                                true
                            }
                            SwipeToDismissBoxValue.StartToEnd -> {
                                if (feedArticle.label == null) {
                                    onMoveToPrivate(currentItem)
                                    true
                                }
                                else false
                            }
                            else -> false
                        }
                    })

                    DismissibleRssItem(
                        state = dismissState,
                        modifier = Modifier.animateItemPlacement()
                    ) {
                        LabelledArticleCard(
                            feedArticle,
                            modifier = Modifier.animateItemPlacement(),
                            pubTime = DateParser.formatTime(feedArticle.publishedTime) ?: "",
                        )
                    }
                }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleRssItem(
    state: SwipeToDismissBoxState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    SwipeToDismissBox(
        modifier = modifier,
        state = state,
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        content = content,
        backgroundContent = {
            val color =
                if (state.dismissDirection == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.errorContainer
                else Color.Transparent

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(12.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Lock, "Private")
                Spacer(modifier = Modifier)
                Icon(
                    Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    contentDescription = "delete"
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
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
