package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.LabelData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelsScaffold(
    labelData: List<LabelData>,
    onBackClick: () -> Unit,
    onAddNewLabel: (String) -> Unit,
    onClick: (Long, String) -> Unit
) {
    var showAddLabelDialog by remember { mutableStateOf(false) }
    var labelInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Labels") },
                actions = {
                    TextButton(onClick = { showAddLabelDialog = true }) {
                        Text(text = "Add Label")
                    }
                }, navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(14.dp),
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalItemSpacing = 8.dp,
            modifier = Modifier.padding(paddingValues),
        ) {
            items(labelData) {
                Card(onClick = {
                    onClick(it.id, it.name)
                }) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = it.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = it.count.toString(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        if (showAddLabelDialog) AlertDialog(
            onDismissRequest = {
                showAddLabelDialog = false
                labelInput = ""
            },
            title = { Text(text = "Label") },
            text = {
                TextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    placeholder = { Text(text = "Enter label name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (labelInput.isNotBlank()) {
                        onAddNewLabel(labelInput)
                        showAddLabelDialog = false
                        labelInput = ""
                    }
                }) {
                    Text(text = "Add")
                }
            })
    }
}

@Preview
@Composable
private fun LabelScaffoldPreview() {
    TheSecretDairyTheme {
        val list = remember {
            mutableStateListOf(
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
                LabelData(1, "Favorites", 23, false),
            )
        }
        LabelsScaffold(labelData = list, {}, {}) { _, _ -> }
    }
}

