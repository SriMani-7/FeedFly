@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.NavigationRouter
import srimani7.apps.feedfly.core.design.FeedFlyTheme
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun HomeScreenScaffold(navigate: (String) -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            HomeAppbar(null, navigate)
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                content = { Icon(Icons.Default.Add, null) },
                onClick = { navigate(NavigationRouter.newFeedRoute()) })
        },
        content = content
    )
}

@Composable
fun HomeScreen(
    homeViewModal: HomeViewModal,
    navigate: (String) -> Unit
) {
    var selectedGroup by rememberSaveable { mutableStateOf<String?>(null) }
    HomeScreenScaffold(navigate) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(12.dp, 6.dp, 12.dp, 100.dp)
        ) {
            item {
                ElevatedAssistChip(
                    onClick = {

                    },
                    label = { Text(selectedGroup ?: "All groups") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, "Drop down")
                    }
                )
            }
            // TODO: display articles
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
                    onDoubleClick = { navigate(NavigationRouter.privateSpaceRoute()) },
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


@Preview
@Composable
private fun HomeScreenPreview() {
    FeedFlyTheme {
        HomeScreenScaffold({}) { paddingValues ->
            LazyColumn (
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(12.dp, 6.dp, 12.dp, 100.dp)
            ) {
                item {
                    ElevatedAssistChip(
                        onClick = {

                        },
                        label = { Text("All groups") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Drop down")
                        }
                    )
                }
                // TODO: display articles
            }
        }
    }
}