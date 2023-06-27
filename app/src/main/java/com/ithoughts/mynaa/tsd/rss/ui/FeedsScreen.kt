@file:OptIn(ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ithoughts.mynaa.tsd.rss.DateParser
import com.ithoughts.mynaa.tsd.rss.FeedsViewModal
import com.ithoughts.mynaa.tsd.rss.db.Feed

@Composable
fun FeedsScreen(feedsViewModal: FeedsViewModal, navController: NavController) {
    val allFeeds by feedsViewModal.allFeeds().collectAsState(initial = null)
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }, topBar = {
            TopAppBar(
                title = {
                    Text(text = "Feed reader")
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(10.dp, 16.dp)
            ) {
                allFeeds?.let {
                    items(it) { feed ->
                        FeedCard(feed = feed, onClick = {
                            navController.navigate(Screens.ArticleScreen.route + "/${feed.id}")
                        })
                    }
                }
            }
            AnimatedVisibility(visible = feedsViewModal.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        if (showDialog) {
            AddUrlDialog(onDismiss = { showDialog = false }, onSubmit = {
                showDialog = false
                feedsViewModal.insertFeed(it)
            })
        }
    }
}

@Composable
fun FeedCard(feed: Feed, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            DateParser.formatDate(feed.lastBuildDate)?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(0.dp, 4.dp)
                )
            }
            Text(
                text = feed.title,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(3.dp))
            feed.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AddUrlDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var url by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter url") },
        confirmButton = {
            TextButton(onClick = {
                if (url.matches(URL_REGEX.toRegex()))
                    onSubmit(url)
                else {
                    isError = true
                    errorText = "Invalid url address"
                }
            }) {
                Text(text = "Next")
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(text = "Cancel")
            }
        }, text = {
            TextField(
                value = url,
                onValueChange = { url = it },
                isError = isError,
                supportingText = { Text(errorText) },
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    if (url.matches(URL_REGEX.toRegex()))
                        onSubmit(url)
                    else {
                        isError = true
                        errorText = "Invalid url address"
                    }
                }),
                maxLines = 10
            )
        }
    )
}

const val URL_REGEX =
    "\\b((?:https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?)"