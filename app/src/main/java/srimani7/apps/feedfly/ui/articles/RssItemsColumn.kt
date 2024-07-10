package srimani7.apps.feedfly.ui.articles

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RssItemsColumn(
    dateListMap: List<LabelledArticle>,
    articlePreference: UserSettingsRepo.ArticlePreference,
    onDeleteArticle: (Long) -> Unit,
    onLongClick: (Long) -> Unit,
    onChangeArticleLabel: (Long, Long?) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(8.dp, 16.dp),
            state = lazyListState
        ) {
            items(dateListMap,
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

                        else -> false
                    }
                }, positionalThreshold = { it * .25f })

                DismissibleRssItem(
                    state = dismissState,
                    dismissRight = articlePreference.swipeToDelete,
                    modifier = Modifier.animateItemPlacement()
                ) {
                    LabelledArticleCard(
                        feedArticle,
                        modifier = Modifier.animateItemPlacement(),
                        pubTime = DateParser.formatDate(feedArticle.publishedTime, true) ?: "",
                        onLongClick = {
                            if (articlePreference.longClickToPrivate) onLongClick(it)
                        }, onOptionClick = {
                            when (it) {
                                "delete" -> onDeleteArticle(feedArticle.articleId)
                                "private" -> onLongClick(feedArticle.articleId)
                            }
                        },
                        onChangeArticleLabel = onChangeArticleLabel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleRssItem(
    state: SwipeToDismissBoxState,
    dismissRight: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    SwipeToDismissBox(
        modifier = modifier,
        state = state,
        enableDismissFromEndToStart = dismissRight,
        enableDismissFromStartToEnd = false,
        content = content,
        backgroundContent = {
            val direction = state.dismissDirection
            val color by animateColorAsState(
                when (state.targetValue) {
                    SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.tertiaryContainer
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                }
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.Settled -> Alignment.CenterStart

                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.Settled -> ImageVector.vectorResource(R.drawable.baseline_label_24)

                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (state.targetValue == SwipeToDismissBoxValue.Settled)
                    0.75f else 1.5f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = contentColorFor(color),
                    modifier = Modifier.scale(scale)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RssItemsColumn(
    channelList: List<ChannelItem>,
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
                    onPlayAudio = {}
                )
            }
        }
    }
}
