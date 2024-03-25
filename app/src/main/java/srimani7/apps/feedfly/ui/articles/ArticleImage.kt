package srimani7.apps.feedfly.ui.articles

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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

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