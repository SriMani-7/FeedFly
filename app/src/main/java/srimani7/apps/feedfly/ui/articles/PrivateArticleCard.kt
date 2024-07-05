package srimani7.apps.feedfly.ui.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.model.PrivateArticle
import srimani7.apps.feedfly.ui.fromHtml
import srimani7.apps.rssparser.DateParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateArticleCard(
    article: PrivateArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(article.publishedTime) ?: "",
    onUnLock: (Long) -> Unit
) {
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = {
            scope.launch { articleModalState.partialExpand() }
        }
    ) {
        Column {
            if (descriptionUri != null && !article.isImage) {
                ArticleImage(descriptionUri!!)

            } else if (article.mediaType != null && article.mediaSrc != null) {
                ArticleMediaHeader(article.mediaType!!, article.mediaSrc!!, {})
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp, 16.dp, 0.dp, 10.dp)
                ) {
                    ArticleTitle(title = article.title)
                    Text(
                        text = pubTime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light
                    )
                }
                IconButton(onClick = { onUnLock(article.articleId) }) {
                    Icon(painterResource(R.drawable.rounded_lock_open_right_24), null)
                }
            }

            if (descriptionUri == null && !article.isImage) Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
            ) {
                ArticleDescription(description = article.description)
            }
        }
        if (articleModalState.isVisible) {
            ArticleViewScreen(
                description = article.description,
                link = article.articleLink,
                sheetState = articleModalState,
                onDismiss = {
                    scope.launch { articleModalState.hide() }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        fromHtml(article.description ?: "") {
            if (descriptionUri == null) descriptionUri = it
            null
        }.toString()
    }
}