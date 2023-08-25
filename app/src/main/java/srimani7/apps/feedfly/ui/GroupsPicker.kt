@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package srimani7.apps.feedfly.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun GroupsPicker(
    selected: String,
    groups: List<String>,
    state: MutableState<Boolean>,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    addNew: Boolean = false,
    onPick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf("") }

    if (state.value) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }
                    .invokeOnCompletion { state.value = false }
            },
            sheetState = bottomSheetState,
            tonalElevation = 1.dp,
            windowInsets = WindowInsets.navigationBars,
            dragHandle = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BottomSheetDefaults.DragHandle()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Select group",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(ButtonDefaults.TextButtonWithIconContentPadding)
                        )
                        if (addNew)
                            TextButton(
                                onClick = { showDialog = true },
                                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "add",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text(text = "New group")
                            }
                    }
                    Divider()
                }
            }
        ) {
            FlowRow(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                groups.forEach {
                    ElevatedFilterChip(selected = selected == it, onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion { _ ->
                            onPick(it)
                            state.value = false
                        }
                    }, label = {
                        Text(
                            it,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(8.dp)
                        )
                    }, elevation = FilterChipDefaults.filterChipElevation())
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
    if (showDialog) AlertDialog(
        onDismissRequest = { showDialog = false },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    if (prompt.isNotBlank()) {
                        showDialog = false
                        onPick(prompt)
                        state.value = false
                    }
                }
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog = false }) {
                Text("Cancel")
            }
        }, title = {
            Text("New feed group")
        }, text = {
            TextField(value = prompt, onValueChange = { prompt = it })
        }
    )
}