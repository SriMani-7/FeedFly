@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.database.FeedArticle
import srimani7.apps.feedfly.ui.ArticleFavoriteToggle
import srimani7.apps.feedfly.ui.DescriptionWebView
import srimani7.apps.feedfly.ui.openInBrowser
import srimani7.apps.feedfly.ui.shareText

@Composable
fun ArticleViewScreen(
    feedArticle: FeedArticle,
    sheetState: SheetState,
    onPinChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomAppBar(
                actions = {
                    ArticleFavoriteToggle(pinned = feedArticle.pinned) {
                        onPinChange(it)
                    }
                    listOf(
                        "Share" to R.drawable.share_24px,
                        "browser" to R.drawable.open_in_browser_24px
                    ).forEach {
                        IconButton(onClick = {
                            when (it.first) {
                                "browser" -> openInBrowser(feedArticle.link, context)
                                "Share" -> shareText(feedArticle.link, context)
                            }
                        }) {
                            Icon(painterResource(it.second), it.first)
                        }
                    }
                }, floatingActionButton = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
            )
        }
    ) {
        Divider()
        if (feedArticle.description != null)
            AndroidView(
                factory = {
                    DescriptionWebView(it, feedArticle.description)
                },
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
    }
}

