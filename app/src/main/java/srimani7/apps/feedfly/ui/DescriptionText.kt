@file:OptIn(ExperimentalFoundationApi::class)

package srimani7.apps.feedfly.ui

import android.text.Html
import android.text.SpannableStringBuilder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import srimani7.apps.feedfly.R

@Composable
fun DescriptionText(
    description: String,
    modifier: Modifier = Modifier,
    maxLines: Int? = Int.MAX_VALUE,
    imageGetter: Html.ImageGetter,
) {
    var spanned by remember { mutableStateOf<AnnotatedString?>(null) }
    spanned?.let {
        maxLines?.let { it1 ->
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSystemInDarkTheme()) FontWeight.Light else FontWeight.Normal,
                modifier = modifier,
                maxLines = it1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    LaunchedEffect(Unit) {
        spanned = (fromHtml(description, imageGetter) as SpannableStringBuilder).toAnnotatedString()
    }
}

@Composable
fun ShowImageDialog(
    imageSrc: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, _ ->
            scale *= zoomChange
            offset += offsetChange
        }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageSrc)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.baseline_photo_size_select_actual_24),
                contentDescription = "image",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(transformableState),
                filterQuality = FilterQuality.High,
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Close, "close")
            }
        }
    }
    LaunchedEffect(scale) {
        if (scale < 0.7f) onDismiss()
    }
}