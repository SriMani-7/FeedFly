@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class
)

package srimani7.apps.feedfly.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.database.FeedArticle
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.feedfly.viewmodel.RssViewModal
import srimani7.apps.rssparser.DateParser


data class AudioMetaData(
    val title: CharSequence,
    val artist: CharSequence,
    val artworkWork: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioMetaData

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (artworkWork != null) {
            if (other.artworkWork == null) return false
            if (!artworkWork.contentEquals(other.artworkWork)) return false
        } else if (other.artworkWork != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (artworkWork?.contentHashCode() ?: 0)
        return result
    }
}

data class SongState(
    val duration: Long = 0,
    val isPlaying: Boolean = false
)

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    internal val mExoPlayer = ExoPlayer.Builder(application).build()

    var audioMetaData by mutableStateOf<AudioMetaData?>(null)
        private set

    var songState by mutableStateOf<SongState?>(null)

    init {
        mExoPlayer.prepare()
        mExoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                songState = SongState(
                    duration = mExoPlayer.currentPosition,
                    isPlaying = isPlaying
                )
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                RssViewModal.info(mediaMetadata)
                audioMetaData = AudioMetaData(
                    mediaMetadata.title ?: mediaMetadata.albumTitle ?: "",
                    mediaMetadata.artist ?: mediaMetadata.albumArtist ?: "",
                    mediaMetadata.artworkData
                )
            }
        })
    }

    fun play(uri: String) {
        mExoPlayer.setMediaItem(MediaItem.fromUri(uri))
        mExoPlayer.playWhenReady = true
    }

    fun play(isPlay: Boolean) {
        if (isPlay) mExoPlayer.play()
        else mExoPlayer.pause()
    }
}

@Composable
fun RssItemsColumn(
    dateListMap: Map<String?, List<FeedArticle>>,
    viewModel: MediaViewModel = viewModel(),
    updateArticle: (Long, Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(10.dp, 15.dp)
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.id }
                ) { feedArticle ->
                    RssItemCard(feedArticle, onPlayAudio = viewModel::play) {
                        updateArticle(feedArticle.id, it)
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
fun RssItemCard(item: FeedArticle, onPlayAudio: (String) -> Unit, onPinChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val pubTime by remember { mutableStateOf(DateParser.formatTime(item.pubDate) ?: "") }

    OutlinedCard(
        onClick = {
            val intent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .build().apply {
                    intent.putExtra(
                        "com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB",
                        true
                    )
                }
            intent.launchUrl(context, Uri.parse(item.link))
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        item.description?.let {
            DescriptionText(it, modifier = Modifier.padding(12.dp, 8.dp)) { src ->
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
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
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
            IconButton(onClick = { shareText(item.link, context) }) {
                Icon(painterResource(R.drawable.share_24px), "Share")
            }
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
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageSrc)
            .build(),
        contentDescription = "image",
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 140.dp),
        filterQuality = FilterQuality.Medium,
    )
}

fun shareText(text: String, context: Context) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, text)
    sendIntent.type = "text/plain"

    val shareIntent = Intent.createChooser(sendIntent, "Share link")
    startActivity(context, shareIntent, ActivityOptionsCompat.makeBasic().toBundle())
}