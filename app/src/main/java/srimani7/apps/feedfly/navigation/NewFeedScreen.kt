@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import srimani7.apps.feedfly.ui.ArticleImage
import srimani7.apps.feedfly.viewmodel.HomeViewModal
import srimani7.apps.rssparser.ParsingState

const val URL_REGEX =
    "\\b((?:https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?)"

@Composable
fun NewFeedScreen(homeViewModal: HomeViewModal, urlF: String?, onDismiss: () -> Unit) {
    var url by remember { mutableStateOf(urlF ?: "") }
    val parseState by homeViewModal.parsingState.collectAsStateWithLifecycle()

    var active by rememberSaveable { mutableStateOf(true) }
    val groups by homeViewModal.groupNameFlow.collectAsState(null)
    var openGroupsPicker by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier,
                query = url,
                onQueryChange = { url = it },
                onSearch = {
                    active = false
                    homeViewModal.fetchFeed(url)
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = { Text(url) },
                leadingIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }, trailingIcon = {
                    IconButton(onClick = { url = "" }) {
                        Icon(Icons.Default.Clear, "Clear")
                    }
                }
            ) {

            }
        }, floatingActionButton = {
            FloatingActionButton(onClick = { openGroupsPicker = true }) {
                Icon(Icons.Default.Done, "done")
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            when (parseState) {
                ParsingState.Completed -> {}
                is ParsingState.Failure -> Text(
                    text = (parseState as ParsingState.Failure).exception.message ?: "Try agin"
                )

                ParsingState.LastBuild -> {}
                ParsingState.Processing -> CircularProgressIndicator()
                is ParsingState.Success -> {
                    val success = parseState as ParsingState.Success
                    Row {
                        AsyncImage(model = success.channel.image?.url, contentDescription = "")
                        Column {
                            Text(text = success.channel.title ?: "No Title")
                            Text(text = success.channel.copyright ?: "")
                        }
                    }
                    LazyColumn {
                        success.channel.items.forEach {
                            item {
                                Column {
                                    it.enclosure?.url?.let { it1 -> ArticleImage(imageSrc = it1) }
                                    Text(text = it.title ?: "")
                                }
                            }
                        }
                    }
                    if (openGroupsPicker) GroupsPicker(
                        bottomSheetState,
                        groups,
                        true,
                        { openGroupsPicker = false }) {
                        homeViewModal.save(success.channel, url, it)
                    }
                }
            }
        }
    }
}

