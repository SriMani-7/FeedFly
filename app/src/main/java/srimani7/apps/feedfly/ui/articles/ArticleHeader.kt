package srimani7.apps.feedfly.ui.articles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ArticleHeader(dateString: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedAssistChip(
            onClick = {},
            label = {
                Text(
                    modifier = Modifier,
                    text = dateString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        )
    }
}