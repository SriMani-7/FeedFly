package srimani7.apps.feedfly.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun ArticleDescription(description: String?) {
    if (!description.isNullOrBlank()) Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Light,
//        color = MaterialTheme.colorScheme.onSurface.copy(.8f)
    )
}