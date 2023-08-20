@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import srimani7.apps.feedfly.ui.theme.TheSecretDairyTheme

@Composable
fun RemoveArticlesScreen(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    val days = remember { mutableIntStateOf(7) }
    Surface(
        shape = AlertDialogDefaults.shape
    ) {
        Column(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Remove old articles", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Deleting articles is irreversible and cannot be undone. Proceed with caution.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TimeRangePicker(
                modifier = Modifier.padding(18.dp, 4.dp),
                days,
                message = "Articles older than",
                items = listOf(7, 14, 30, 2, 5)
            )
            Divider()
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(18.dp, 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
                Button(onClick = { onSubmit(days.intValue) }) {
                    Text(text = "Delete")
                }
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
        Dialog(onDismissRequest = {  }) {
            RemoveArticlesScreen({}) {}
        }
    }
}

@Composable
fun TimeRangePicker(
    modifier: Modifier,
    days: MutableIntState,
    message: String,
    items: List<Int>
) {
    var showDropdown by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(message, style = MaterialTheme.typography.labelLarge)
        ElevatedAssistChip(onClick = { showDropdown = true }, label = {
            Text(text = "${days.intValue} days")
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }) {
                items.forEach {
                    DropdownMenuItem(text = { Text(text = "$it days") }, onClick = {
                        days.intValue = it
                        showDropdown = false
                    })
                }
            }
        }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") })
    }
}

