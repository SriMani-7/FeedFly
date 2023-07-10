package srimani7.apps.feedfly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ArticleHeader(
    dateString: String,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentColor: Color = contentColorFor(color)
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .background(color, RoundedCornerShape(12.dp))
                .padding(14.dp, 5.dp),
            text = dateString,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}