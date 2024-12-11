package srimani7.apps.feedfly.ui.articles

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import srimani7.apps.feedfly.core.database.entity.ArticleMedia

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