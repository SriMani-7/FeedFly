package srimani7.apps.feedfly.ui.articles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import srimani7.apps.feedfly.audio.MediaViewModel
import srimani7.apps.feedfly.core.database.dto.FeedArticle
import srimani7.apps.feedfly.ui.ExoPlayerCard
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RssItemsColumn(
    dateListMap: Map<String?, List<FeedArticle>>,
    viewModel: MediaViewModel = viewModel(),
    updateArticle: (Long, Boolean) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(8.dp, 15.dp),
            state = lazyListState
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        if (entry.value.size >= 2) ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.id },
                    contentType = { "article" }
                ) { feedArticle ->
                    RssItemCard(
                        feedArticle,
                        modifier = Modifier.animateItemPlacement(),
                        onPlayAudio = viewModel::play,
                        pubTime = remember {
                            if (entry.value.size < 2) {
                                DateParser.formatDate(feedArticle.pubDate, false) ?: ""
                            } else {
                                DateParser.formatTime(feedArticle.pubDate) ?: ""
                            }
                        },
                    ) { updateArticle(feedArticle.id, it) }
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
