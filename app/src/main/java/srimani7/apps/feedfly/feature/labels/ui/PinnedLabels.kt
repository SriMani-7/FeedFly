package srimani7.apps.feedfly.feature.labels.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.core.model.LabelData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PinnedLabels(labels: List<LabelData>, onLongClick: (Long) -> Unit, onViewAll: () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEach { label ->
            ElevatedButton(
                onClick = { onLongClick(label.id) },
                contentPadding = PaddingValues(16.dp, 10.dp)
            ) {
                Text(label.name, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = label.count.toString(), fontWeight = FontWeight.Bold)
            }
        }
        TextButton(
            onClick = onViewAll,
        ) {
            Text("All Labels")
        }
    }
}