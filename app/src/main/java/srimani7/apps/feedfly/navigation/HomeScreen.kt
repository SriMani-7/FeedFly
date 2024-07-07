@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import srimani7.apps.feedfly.MainNavigation
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.LabelData
import srimani7.apps.feedfly.feature.labels.ui.PinnedLabels
import srimani7.apps.feedfly.viewmodel.HomeViewModal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModal: HomeViewModal,
    navigate: (String) -> Unit
) {
    val groups by homeViewModal.feedGroupsFlow.collectAsStateWithLifecycle(emptyList())
    val pinnedLabels by homeViewModal.pinnedLabelsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = { HomeAppbar(null, navigate) },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(12.dp, 6.dp, 12.dp, 100.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "pinned-labels") {
                PinnedLabels(labels = pinnedLabels,
                    onLongClick = {},
                    onViewAll = {})
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Groups",
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal
                )
            }

            items(groups, key = { it.name }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigate(MainNavigation.groupOverviewScreen(it.name))
                    },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp, 16.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${it.count} sites",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeAppbar(scrollBehavior: TopAppBarScrollBehavior?, navigate: (String) -> Unit) {
    TopAppBar(
        title = {
            Text(
                "FeedFly",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.combinedClickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onDoubleClick = { navigate(MainNavigation.privateSpaceRoute()) },
                    onClick = {}
                )
            )
        },
        actions = {
            IconButton(onClick = { navigate(Screen.SettingsScreen.destination) }) {
                Icon(Icons.Outlined.Settings, null)
            }
        },
        scrollBehavior = scrollBehavior,
    )
}


@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    val labels = remember {
        "Reading list,Favorites,Gaming,Resume needed,Applied".split(",")
            .mapIndexed { index, s -> LabelData(index.toLong(), s, index + 3, true) }
    }
    TheSecretDairyTheme {
        Scaffold(
            topBar = {
                HomeAppbar(scrollBehavior = null, {})
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier.padding(paddingValues),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(12.dp, 6.dp, 12.dp, 100.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PinnedLabels(labels = labels, {},{})
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Groups",
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}