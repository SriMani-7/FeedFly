@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModal: HomeViewModal
) {
    val allFeeds by homeViewModal.allFeedsFlow.collectAsStateWithLifecycle(initialValue = null)
    val groups by homeViewModal.groupNameFlow.collectAsState(null)
    var openGroupsPicker by remember { mutableStateOf(false) }
    val currentGroup = rememberSaveable { mutableStateOf<String?>(null) }
    val filteredFeeds by remember(currentGroup.value, allFeeds) {
        mutableStateOf(allFeeds?.filter { it.group == currentGroup.value })
    }

    val bottomSheetState = rememberModalBottomSheetState()
    val scrollBehavior = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "FeedFly",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(InsertFeedScreen.route) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
                LazyRow(
                    contentPadding = PaddingValues(12.dp, 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    item {
                        FeedFilterChip(
                            selected = homeViewModal.currentFilter == HomeFilter.ALL,
                            onClick = { homeViewModal.currentFilter = HomeFilter.ALL },
                            label = HomeFilter.ALL.label
                        )
                    }
                    item {
                        FeedFilterChip(
                            selected = homeViewModal.currentFilter == HomeFilter.GROUPED,
                            label = currentGroup.value ?: "Other feeds",
                            trailingIcon = Icons.Default.ArrowDropDown,
                            onClick = {
                                openGroupsPicker = homeViewModal.currentFilter == HomeFilter.GROUPED
                                homeViewModal.currentFilter = HomeFilter.GROUPED
                            }
                        )
                    }
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedVisibility(visible = homeViewModal.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            when (homeViewModal.currentFilter) {
                HomeFilter.ALL -> allFeeds?.let {
                    FeedGroupList(it) { feedId ->
                        navController.navigate(Home.ArticlesScreen.destination + "/${feedId}")
                    }
                }

                HomeFilter.GROUPED -> filteredFeeds?.let {
                    FeedGroupList(it) { feedId ->
                        navController.navigate(Home.ArticlesScreen.destination + "/${feedId}")
                    }
                }
            }

        }
        if (openGroupsPicker) GroupsPicker(bottomSheetState = bottomSheetState,
            groups = groups,
            onPick = { currentGroup.value = it },
            onClose = { openGroupsPicker = false }
        )
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

@Composable
fun GroupsPicker(
    bottomSheetState: SheetState,
    groups: List<String?>?,
    addNew: Boolean = false,
    onClose: () -> Unit,
    onPick: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(0),
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BottomSheetDefaults.DragHandle()
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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
        if (groups != null)
            LazyColumn(
                contentPadding = PaddingValues(10.dp, 12.dp),
            ) {
                items(groups, key = { it ?: "Other feeds" }) {
                    TextButton(
                        onClick = {
                            scope.launch { bottomSheetState.hide() }.invokeOnCompletion { _ ->
                                if (!bottomSheetState.isVisible) {
                                    onPick(it)
                                    onClose()
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text(
                            it ?: "Other feeds",
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxWidth(),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

            }
        Spacer(modifier = Modifier.height(30.dp))
    }
    if (showDialog) AlertDialog(
        onDismissRequest = { showDialog = false },
        confirmButton = {
            TextButton(onClick = {
                if (prompt.isNotBlank()) {
                    showDialog = false
                    onPick(prompt)
                    onClose()
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

sealed class HomeFilter(val label: String) {
    object ALL : HomeFilter("All feeds")
    object GROUPED : HomeFilter("Groups")
}