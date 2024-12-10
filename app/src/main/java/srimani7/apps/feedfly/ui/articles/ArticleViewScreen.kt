package srimani7.apps.feedfly.ui.articles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.ui.DescriptionWebView
import srimani7.apps.feedfly.ui.openInBrowser
import srimani7.apps.feedfly.util.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleViewScreen(
    description: String?,
    link: String,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomAppBar(
                actions = {
                    listOf(
                        "Share" to R.drawable.share_24px,
                        "browser" to R.drawable.open_in_browser_24px
                    ).forEach {
                        IconButton(onClick = {
                            when (it.first) {
                                "browser" -> openInBrowser(link, context)
                                "Share" -> shareText(link, context)
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
        if (description != null)
            AndroidView(
                factory = {
                    DescriptionWebView(it, description)
                },
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) else {
            Text(
                text = "No description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 20.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}