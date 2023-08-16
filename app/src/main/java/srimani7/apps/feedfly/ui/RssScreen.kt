@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)

package srimani7.apps.feedfly.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.audio.AudioMetaData
import srimani7.apps.feedfly.audio.MediaViewModel
import srimani7.apps.feedfly.audio.SongState
import srimani7.apps.feedfly.database.FeedArticle
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.feedfly.navigation.ArticleViewScreen
import srimani7.apps.rssparser.DateParser


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
                        ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.id },
                    contentType = { "article" }
                ) { feedArticle ->
                    RssItemCard(
                        feedArticle,
                        modifier = Modifier.animateItemPlacement(),
                        onPlayAudio = viewModel::play
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

@Composable
fun ExoPlayerCard(
    songState: SongState,
    audioMetaData: AudioMetaData?,
    playToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(95.dp, 80.dp)) {
                audioMetaData?.artworkWork?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Artwork",
                        contentScale = ContentScale.Crop
                    )
                }
                FilledTonalIconToggleButton(
                    checked = songState.isPlaying,
                    onCheckedChange = playToggle
                ) {
                    val vector =
                        if (songState.isPlaying) ImageVector.vectorResource(R.drawable.pause_circle_24px) else Icons.Default.PlayArrow
                    Icon(vector, "Pause", modifier = Modifier.size(48.dp))
                }
            }
            audioMetaData?.let {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        it.artist.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        it.title.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RssItemCard(
    item: FeedArticle,
    modifier: Modifier = Modifier,
    onPlayAudio: (String) -> Unit,
    onPinChange: (Boolean) -> Unit
) {
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val pubTime by remember { mutableStateOf(DateParser.formatTime(item.pubDate) ?: "") }
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
            if (descriptionUri != null && item.articleMedia?.urlType != ArticleMedia.MediaType.IMAGE)
                ArticleImage(descriptionUri!!)
        }
        item.articleMedia?.let { ArticleMediaHeader(it, onPlayAudio) }
        Column(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.title.isNotBlank()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.category.isNotBlank())
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Text(
                text = pubTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.weight(1f))
            ArticleFavoriteToggle(item.pinned) { onPinChange(it) }
        }
    }
    if (articleModalState.isVisible) {
        ArticleViewScreen(item, articleModalState, onPinChange) {
            scope.launch { articleModalState.hide() }
        }
    }
}

@Composable
fun ArticleMediaHeader(
    articleMedia: ArticleMedia,
    playAudio: (String) -> Unit
) {
    val imageSrc by rememberSaveable { mutableStateOf(articleMedia.url) }
    val mediaType by rememberSaveable { mutableStateOf(articleMedia.urlType) }

    when (mediaType) {
        ArticleMedia.MediaType.IMAGE -> imageSrc?.let { ArticleImage(it) }
        ArticleMedia.MediaType.AUDIO -> {
            IconButton(onClick = { articleMedia.url?.let { playAudio(it) } }) {
                Icon(Icons.Filled.PlayArrow, "Play")
            }
        }

        else -> {}
    }
}

@Composable
fun ArticleImage(imageSrc: String) {
    var shoImage by remember { mutableStateOf(false) }
    AsyncImage(
        model = imageSrc.replaceFirst("http:", "https:"),
        contentDescription = "image",
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 8f)
            .clickable { shoImage = true },
        filterQuality = FilterQuality.Medium,
        transform = {
            AsyncImagePainter.DefaultTransform.invoke(it)
        }
    )
    if (shoImage) ShowImageDialog(imageSrc = imageSrc) {
        shoImage = false
    }
}

fun shareText(text: String, context: Context) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, text)
    sendIntent.type = "text/plain"

    val shareIntent = Intent.createChooser(sendIntent, "Share link")
    startActivity(context, shareIntent, ActivityOptionsCompat.makeBasic().toBundle())
}