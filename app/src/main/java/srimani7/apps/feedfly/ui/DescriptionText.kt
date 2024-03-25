@file:OptIn(ExperimentalFoundationApi::class)

package srimani7.apps.feedfly.ui

import android.text.Html.ImageGetter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.text.HtmlCompat

@Composable
fun HtmlImage(text: String, imageGetter: ImageGetter) {
    LaunchedEffect(Unit) {
        HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT, imageGetter, null)
    }
}

