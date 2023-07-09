@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import srimani7.apps.feedfly.BackButton
import srimani7.apps.feedfly.rss.DateParser
import srimani7.apps.feedfly.rss.ParsingState
import srimani7.apps.feedfly.ui.RssItemsColumn
import srimani7.apps.feedfly.viewmodel.RssViewModal

@Composable
fun RssScreen(feedId: Long, navController: NavHostController) {
    val context = LocalContext.current
    val viewModal = viewModel(initializer = {
        RssViewModal(feedId, (context as Activity).application)
    })
    val parsingState by viewModal.parsingState.collectAsState()
    val feedArticles by viewModal.groupedArticles.collectAsState(initial = null)
    val feed by viewModal.feed.collectAsState(initial = null)

    val hostState = remember { SnackbarHostState() }

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
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            feedArticles?.let {
                RssItemsColumn(it) { articleItem ->
                    viewModal.updateArticle(articleItem)
                }
            }
            AnimatedVisibility(parsingState == ParsingState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }

    LaunchedEffect(parsingState) {
        when (parsingState) {
            is ParsingState.Error -> {
                RssViewModal.info((parsingState as ParsingState.Error).message)
                hostState.showSnackbar(
                    (parsingState as ParsingState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> {}
        }
    }
}