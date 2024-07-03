package srimani7.apps.feedfly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.audio.AudioMetaData
import srimani7.apps.feedfly.audio.SongState

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