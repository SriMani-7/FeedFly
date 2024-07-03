package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import srimani7.apps.feedfly.core.model.LabelData

@Composable
fun ChangeArticleLabelDialog(
    pLabelId: Long?,
    labels: List<LabelData>,
    update: (Long) -> Unit,
    cancel: () -> Unit
) {
    var labelId by remember { mutableLongStateOf(pLabelId ?: -1L) }

    AlertDialog(
        onDismissRequest = cancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        title = { Text(text = "Select label") },
        text = {
            LazyColumn {
                item(key = "None") {
                    TextButton(onClick = { labelId = -1L }) {
                        Text(text = "None")
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = labelId == -1L,
                            onClick = {
                                labelId = -1L
                            }
                        )
                    }
                }
                items(labels, key = {it.id}) { labelData ->
                    TextButton(onClick = { labelId = labelData.id }) {
                        Text(text = labelData.name)
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = labelData.id == labelId,
                            onClick = {
                                labelId = labelData.id
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                update(labelId)
            }) {
                Text(text = "Update")
            }
        }, dismissButton = {
            TextButton(onClick = cancel) {
                Text(text = "Cancel")
            }
        }
    )
}