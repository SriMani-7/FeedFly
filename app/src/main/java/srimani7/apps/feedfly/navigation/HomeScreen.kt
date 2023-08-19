@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.MainNavigation
import srimani7.apps.feedfly.database.FeedDto
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun HomeScreen(
    homeViewModal: HomeViewModal,
    navigate: (String) -> Unit
) {
    val allFeeds by homeViewModal.allFeedsFlow.collectAsStateWithLifecycle()
    val groups by homeViewModal.groupNameFlow.collectAsState()
    val scrollBehavior = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val settings by homeViewModal.settingsStateFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeAppbar(scrollBehavior, navigate) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (groups.isNotEmpty())
                FeedsHome(
                    currentGroup = settings.currentGroup,
                    groups = groups,
                    allFeeds,
                    updateGroup = homeViewModal::updateCurrentGroup
                ) {
                    navigate(MainNavigation.articlesScreenRoute(it))
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

@Composable
fun GroupsPicker(
    selected: String,
    bottomSheetState: SheetState,
    groups: List<String>,
    addNew: Boolean = false,
    onClose: () -> Unit,
    onPick: (String) -> Unit
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            groups.forEach {
                ElevatedFilterChip(selected = selected == it, onClick = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion { _ ->
                        if (!bottomSheetState.isVisible) {
                            onPick(it)
                            onClose()
                        }
                    }
                }, label = {
                    Text(
                        it,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Normal
                    )
                })
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

@Composable
fun HomeAppbar(scrollBehavior: TopAppBarScrollBehavior?, navigate: (String) -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "FeedFly",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.titleLarge
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
    currentGroup: String?,
    groups: List<String>,
    allFeeds: List<FeedDto>,
    updateGroup: (String) -> Unit,
    onClick: (Long) -> Unit
) {
    var selectedGroup by remember { mutableStateOf(currentGroup ?: groups[0]) }
    var showAll by rememberSaveable { mutableStateOf(currentGroup.isNullOrBlank()) }
    var openGroupsPicker by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val filteredFeeds by remember(allFeeds) {
        derivedStateOf {
            allFeeds.filter { it.group == selectedGroup }
        }
    }

    val allListState = rememberLazyListState()
    val listState = rememberLazyListState()

    Column(Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(12.dp, 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            item {
                FeedFilterChip(showAll, "All feeds") { showAll = true }
            }
            item {
                FeedFilterChip(
                    selected = !showAll,
                    label = selectedGroup,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    onClick = {
                        if (!showAll) {
                            openGroupsPicker = true
                        }
                        showAll = false
                    }
                )
            }
        }
        if (showAll) FeedGroupList(allFeeds, allListState, onClick)
        else FeedGroupList(filteredFeeds, listState, onClick)
    }
    if (openGroupsPicker) GroupsPicker(selectedGroup, bottomSheetState = bottomSheetState,
        groups = groups,
        onPick = {
            updateGroup(it)
            selectedGroup = it
        },
        onClose = { openGroupsPicker = false }
    )
}
