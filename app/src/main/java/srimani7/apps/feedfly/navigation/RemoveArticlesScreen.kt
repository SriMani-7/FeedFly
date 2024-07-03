package srimani7.apps.feedfly.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import srimani7.apps.feedfly.ui.NumberCapsule
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme

@Composable
fun RemoveArticlesScreen(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    val days = remember { mutableIntStateOf(7) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier.padding(18.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Remove Old Articles",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Deleting articles is irreversible and cannot be undone. Proceed with caution.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        NumberCapsule(days, Modifier.padding(18.dp, 10.dp), "days ago")
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(18.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
            TextButton(onClick = { onSubmit(days.intValue) }) {
                Text(text = "Confirm")
            }
        }
    }
}

@Preview(
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = false, showSystemUi = true
)
@Preview(
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    showBackground = false, showSystemUi = true,
)
@Composable
fun CleanPreview() {
    TheSecretDairyTheme {
        Dialog(onDismissRequest = { }) {
            RemoveArticlesScreen({}) {}
        }
    }
}

