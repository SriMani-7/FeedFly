package srimani7.apps.feedfly.audio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import srimani7.apps.feedfly.viewmodel.RssViewModal

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