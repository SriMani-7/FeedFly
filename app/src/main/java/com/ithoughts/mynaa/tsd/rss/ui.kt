@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalTextApi::class
)

package com.ithoughts.mynaa.tsd.rss

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RssScreen(viewModal: RssViewModal = viewModel()) {
    val parsingState by viewModal.parsingState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily news") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "add")
            }
        }
    ) { paddingValues ->
        when (parsingState) {
            is ParsingState.Error -> ErrorScreen()
            ParsingState.Loading -> LoadingScreen()
            is ParsingState.Success -> {
                val rss = (parsingState as ParsingState.Success).rss
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(12.dp, 16.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    rss?.list?.forEach {
                        stickyHeader {
                            Surface(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp, 10.dp)) {
                                    Text(text = it.title ?: "")
                                    Text(text = it.description ?: "")
                                    it.link?.let { it1 -> HyperlinkText(link = it1) }
                                }
                            }
                        }
                        it.list?.forEach { item { RssItemCard(it) } }
                    }
                }
            }
        }
    }
    AnimatedVisibility(showDialog) {
        AddUrlDialog(onDismiss = { showDialog = false }, onSubmit = {
            showDialog = false
            viewModal.parseXml(it)
        })
    }
}

@Composable
fun RssItemCard(item: Rss.Channel.Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp, 12.dp)
    ) {
        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = item.category, style = MaterialTheme.typography.labelMedium)
        HyperlinkText(link = item.guid, modifier = Modifier.padding(3.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RssItemPreview() {
    RssItemCard(
        item = Rss.Channel.Item(
            title = "ANANTH TECHNOLOGIES IS HIRING: DIRECT WALKIN DRIVE",
            link = "https://frontlinesmedia.in/ananth-technologies-is-hiring-direct-walkin-drive/",
            category = "Job Notifications",
            guid = "https://frontlinesmedia.in/?p=8639"
        )
    )
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showSystemUi = true, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Composable
fun ErrorScreen(error: String = "Something went wrong", onRetry: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(error, style = MaterialTheme.typography.bodyLarge)
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
                maxLines = 10, minLines = 3,
            )
        }
    )
}

const val URL_REGEX =
    "\\b((?:https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?)"

@Preview(showBackground = true)
@Composable
fun AddUrlDialogPreview() {
    AddUrlDialog(onDismiss = { }, onSubmit = {})
}

@Composable
fun HyperlinkText(
    link: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(
            color = Color.Blue
        )) {
            append(link)
        }
        addStringAnnotation(
            "URL", link, 0, link.length
        )
    }
    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}