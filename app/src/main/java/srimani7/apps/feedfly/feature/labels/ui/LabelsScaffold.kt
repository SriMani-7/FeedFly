package srimani7.apps.feedfly.feature.labels.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.LabelData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelsScaffold(
    labelData: List<LabelData>,
    onClick: (Long, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Labels") })
        }
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(14.dp),
            columns = StaggeredGridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalItemSpacing = 4.dp,
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
                        Text(text = it.count.toString(), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LabelScaffoldPreview() {
    TheSecretDairyTheme {
        val list = remember {
            mutableStateListOf(
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
                LabelData(1, "Favorites", 23, 0),
            )
        }
        LabelsScaffold(labelData = list) { _, _ -> }
    }
}

