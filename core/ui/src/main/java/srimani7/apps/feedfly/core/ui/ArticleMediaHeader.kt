package srimani7.apps.feedfly.core.ui

import androidx.compose.runtime.Composable

@Composable
fun ArticleMediaHeader(
    mediaType: String,
    mediaSrc: String
) {
    when {
        mediaType.contains("image") -> ArticleImage(mediaSrc)

        else -> {}
    }
}