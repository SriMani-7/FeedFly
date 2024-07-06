@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import srimani7.apps.feedfly.MainNavigation
import srimani7.apps.feedfly.core.database.dto.FeedDto
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.feedfly.viewmodel.HomeViewModal


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeViewModal: HomeViewModal,
    navigate: (String) -> Unit
) {
    val allFeeds by homeViewModal.allFeedsFlow.collectAsStateWithLifecycle()
    val groups by homeViewModal.feedGroupsFlow.collectAsStateWithLifecycle(emptyList())
    val scrollBehavior = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val settings = homeViewModal.settingsStateFlow.collectAsStateWithLifecycle()
    val groupPickerState = remember { mutableStateOf(false) }

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
            contentPadding = PaddingValues(14.dp, 12.dp, 14.dp, 100.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Groups",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
            }

            items(groups, key = { it.name }) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
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

@Composable
fun FeedFilterChip(
    selected: Boolean,
    label: String,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    ElevatedFilterChip(
        selected = selected,
        onClick = { onClick() },
        label = { Text(label) },
        trailingIcon = {
            trailingIcon?.let { Icon(it, "Drop down") }
        }
    )
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
            IconButton(onClick = { navigate(MainNavigation.newFeedRoute()) }) {
                Icon(Icons.Default.Add, "Add")
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun FeedsHome(
    currentGroup: State<UserSettingsRepo.Settings>,
    allFeeds: List<FeedDto>,
    openGroupPicker: () -> Unit,
    onClick: (Long) -> Unit
) {

    val settings by remember { currentGroup }
    var selectedIndex by remember { mutableIntStateOf(if (settings.currentGroup.isBlank()) 0 else 1) }
    val filteredFeeds by remember {
        derivedStateOf { allFeeds.filter { it.group == settings.currentGroup } }
    }

    val allListState = rememberLazyListState()
    val listState = rememberLazyListState()

    Column(Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(12.dp, 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            item {
                FeedFilterChip(selectedIndex == 0, "All feeds") {
                    selectedIndex = 0
                }
            }
            item {
                FeedFilterChip(
                    selected = selectedIndex == 1,
                    label = settings.currentGroup,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    onClick = {
                        if (selectedIndex == 1) openGroupPicker()
                        selectedIndex = 1
                    }
                )
            }
        }
        Crossfade(selectedIndex, label = "", animationSpec = spring()) {
            when (it) {
                0 -> FeedGroupList(allFeeds, allListState, onClick)
                1 -> FeedGroupList(filteredFeeds, listState, onClick)
            }
        }
    }
}