package srimani7.apps.feedfly.ui.articles

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun ArticleMediaHeader(
    mediaType: String,
    mediaSrc: String,
    playAudio: (String) -> Unit
) {
    when {
        mediaType.contains("image") -> ArticleImage(mediaSrc)
        mediaType.contains("audio") -> {
            IconButton(onClick = { playAudio(mediaSrc) }) {
                Icon(Icons.Filled.PlayArrow, "Play")
            }
        }

        else -> {}
    }
}