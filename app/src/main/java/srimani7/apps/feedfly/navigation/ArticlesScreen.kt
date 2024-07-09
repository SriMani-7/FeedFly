@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import srimani7.apps.feedfly.BackButton
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.feedfly.ui.GroupsPicker
import srimani7.apps.feedfly.ui.articles.RssItemsColumn
import srimani7.apps.feedfly.viewmodel.ArticlesUIState
import srimani7.apps.feedfly.viewmodel.RssViewModal
import srimani7.apps.rssparser.DateParser

@Composable
fun ArticlesScreen(navController: NavHostController) {
    val viewModal = viewModel<RssViewModal>()
    val articlePreference by viewModal.articlePreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserSettingsRepo.ArticlePreference()
    )

    val parsingState by viewModal.uiStateStateFlow.collectAsState()
    val feed by viewModal.feedStateFlow.collectAsState(initial = null)
    val articleLabels by viewModal.articlesLabelsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedLabel by viewModal.selectedLabel
    val articles by viewModal.articles.collectAsStateWithLifecycle(initialValue = emptyList())

    val hostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val groups by viewModal.groupNameFlow.collectAsState()
    val openGroupsPicker = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = { BackButton(navController) },
                    title = {
                        Column {
                            feed.also { feed ->
                                feed?.title?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                DateParser.formatDate(feed?.lastBuildDate)?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }, actions = {
                        FeedActions(
                            options = listOf(
                                "Delete",
                                "Refresh",
                                "Change Group",
                                "Remove old articles"
                            )
                        ) {
                            when (it) {
                                "Delete" -> viewModal.delete(feed)
                                "Refresh" -> viewModal.refresh(feed)
                                "Change Group" -> openGroupsPicker.value = true
                                "Remove old articles" -> navController.navigate(Screen.RemoveArticlesScreen.destination + "/" + feed?.id)
                            }
                        }
                    }, scrollBehavior = scrollBehavior
                )
                LazyRow(
                    contentPadding = PaddingValues(8.dp, 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(articleLabels, key = { it.id }) {
                        ElevatedFilterChip(
                            selected = selectedLabel == it.id,
                            onClick = { viewModal.applyLabelFilter(it.id) },
                            label = { Text(it.name) },
                            trailingIcon = { if(selectedLabel == it.id) Text(it.count.toString())}
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .nestedScroll(scrollBehavior.nestedScrollConnection)) {
            when (parsingState) {
                ArticlesUIState.COMPLETED, is ArticlesUIState.Failure -> RssItemsColumn(
                    dateListMap = articles,
                    articlePreference = articlePreference,
                    onDeleteArticle = viewModal::deleteArticle,
                    onLongClick = viewModal::onMoveToPrivate,
                    onChangeArticleLabel = { aId, lId ->
                        navController.navigate(Screen.ChangeLabelDialog.destination + "/$aId?label=${lId ?: -1L}")
                    }
                )

                ArticlesUIState.Loading -> AnimatedVisibility(parsingState == ArticlesUIState.Loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        if (feed != null) GroupsPicker(
            selected = feed!!.group,
            groups = groups.ifEmpty { listOf("Others") },
            state = openGroupsPicker,
            addNew = true
        ) {
            viewModal.updateFeed(feed?.copy(group = it))
        }
    }

    LaunchedEffect(parsingState) {
        val message =
            (parsingState as? ArticlesUIState.Failure)?.message ?: return@LaunchedEffect
        hostState.showSnackbar(message, duration = SnackbarDuration.Short)
    }

    LaunchedEffect(feed) {
        feed?.let { viewModal.parseXml(it) }
    }
}

@Composable
fun FeedActions(options: List<String>, onMenuClick: (String) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded = true }) {
        Icon(Icons.Default.MoreVert, "Options")
        DropdownMenu(
            expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
            modifier = Modifier.defaultMinSize(minWidth = 120.dp)
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = { menuExpanded = false; onMenuClick(it) },
                    contentPadding = PaddingValues(12.dp, 8.dp),
                )
            }
        }
    }
}