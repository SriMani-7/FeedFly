@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import srimani7.apps.feedfly.BackButton
import srimani7.apps.feedfly.ui.RssItemsColumn
import srimani7.apps.feedfly.viewmodel.ArticlesUIState
import srimani7.apps.feedfly.viewmodel.RssViewModal
import srimani7.apps.rssparser.DateParser

@Composable
fun RssScreen(feedId: Long, navController: NavHostController) {
    val context = LocalContext.current
    val viewModal = viewModel(initializer = {
        RssViewModal(feedId, (context as Activity).application)
    })
    val parsingState by viewModal.uiStateStateFlow.collectAsState()
    val feedArticles by viewModal.groupedArticles.collectAsState(initial = null)
    val feed by viewModal.feedStateFlow.collectAsState(initial = null)

    val hostState = remember { SnackbarHostState() }

    val groups by viewModal.groupNameFlow.collectAsState(null)
    var openGroupsPicker by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
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
                    FeedActions(options = listOf("Delete", "Refresh", "Change Group")) {
                        when (it) {
                            "Delete" -> viewModal.delete(feed)
                            "Refresh" -> viewModal.refresh(feed)
                            "Change Group" -> openGroupsPicker = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            feedArticles?.let {
                RssItemsColumn(it) { id, changed ->
                    viewModal.updateArticle(id, changed)
                }
            }
            AnimatedVisibility(parsingState == ArticlesUIState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (openGroupsPicker) GroupsPicker(
            bottomSheetState,
            groups,
            true,
            { openGroupsPicker = false }) {
            viewModal.updateFeed(feed?.copy(group = it))
        }
    }

    LaunchedEffect(parsingState) {
        val message = when (parsingState) {
            is ArticlesUIState.Failure -> (parsingState as ArticlesUIState.Failure).message
            ArticlesUIState.COMPLETED -> "Fetching completed"
            else -> return@LaunchedEffect
        }
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