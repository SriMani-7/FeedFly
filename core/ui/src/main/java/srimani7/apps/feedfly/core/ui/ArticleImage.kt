package srimani7.apps.feedfly.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@Composable
fun ArticleImage(imageSrc: String) {
    var shoImage by remember { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(imageSrc.replaceFirst("http:", "https:"))
    AsyncImage(
        model = painter,
        contentDescription = "image",
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 8.3f)
            .clickable { shoImage = true }.clip(RectangleShape),
        filterQuality = FilterQuality.Medium,
    )
    if (shoImage) ShowImageDialog(imageSrc = imageSrc) {
        shoImage = false
    }
}