package srimani7.apps.feedfly.ui.articles

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.database.dto.FeedArticle
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.navigation.ArticleViewScreen
import srimani7.apps.feedfly.ui.fromHtml
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssItemCard(
    item: FeedArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(item.pubDate) ?: "",
    onPlayAudio: (String) -> Unit
) {
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val description by remember {
        derivedStateOf {
            fromHtml(item.description ?: "") {
                descriptionUri = it
                null
            }.toString()
        }
    }
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Surface(
        onClick = { scope.launch { articleModalState.show() } },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ArticleTitle(title = item.title)
                    Text(
                        text = pubTime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
            ) {

                if (descriptionUri != null && item.articleMedia?.urlType != ArticleMedia.MediaType.IMAGE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ArticleImage(descriptionUri!!)
                    Spacer(modifier = Modifier.height(8.dp))

                }
                else item.articleMedia?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    ArticleMediaHeader(it, onPlayAudio)
                    Spacer(modifier = Modifier.height(8.dp))

                }
                ArticleDescription(description = description)
            }

            Row(modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)) {
                Spacer(modifier = Modifier.weight(1f))
            }

        }
        if (articleModalState.isVisible) {
            ArticleViewScreen(item, articleModalState) {
                scope.launch { articleModalState.hide() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssItemCard(
    item: ChannelItem,
    modifier: Modifier,
    onPlayAudio: (String) -> Unit
) {
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val description by remember {
        derivedStateOf {
            fromHtml(item.description ?: "") {
                descriptionUri = it
                null
            }.toString()
        }
    }
    val pubTime by remember {
        mutableStateOf(
            DateParser.parseDate(item.pubDate)
                ?.let { SimpleDateFormat.getDateTimeInstance().format(it) }
                ?: ""
        )
    }
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    Surface(
        onClick = { scope.launch { articleModalState.show() } },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ArticleTitle(title = item.title)
                    Text(
                        text = pubTime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
            ) {
                if (descriptionUri != null && ArticleMedia.isImage(item.enclosure?.type)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ArticleImage(descriptionUri!!)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else item.enclosure?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    ArticleMediaHeader(ArticleMedia(it, 1), onPlayAudio)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                ArticleDescription(description = description)
            }

        }
        if (articleModalState.isVisible) {
            ArticleViewScreen(item.description, item.link ?: "", articleModalState) {
                scope.launch { articleModalState.hide() }
            }
        }
    }
}


@Composable
fun ArticleTitle(title: String?, modifier: Modifier = Modifier) {
    if (!title.isNullOrBlank()) Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun ArticleDescription(description: String?) {
    if (!description.isNullOrBlank()) Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Light,
//        color = MaterialTheme.colorScheme.onSurface.copy(.8f)
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun RssItemCardPreview() {
    TheSecretDairyTheme(true) {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(8.dp, 16.dp),
            ) {
                item {
                    RssItemCard(
                        item = FeedArticle(
                            title = "How to integrate UPI payments in jetpack compose",
                            link = "",
                            category = "Payments",
                            articleMedia = null,
                            description = "The Text composable has multiple optional parameters to style its content. Below, we’ve listed parameters that cover the most common use cases with text. For all the parameters of Text, see the Compose Text source code."
                        ),
                        pubTime = "22 MARCH 2024",
                        modifier = Modifier,
                        onPlayAudio = {},)
                }
                items(3) {
                    RssItemCard(
                        item = FeedArticle(
                            title = "How to integrate UPI payments in jetpack compose",
                            link = "",
                            category = "Payments",
                            articleMedia = ArticleMedia(
                                type = "",
                                length = null,
                                url = "https://developer.android.com/static/images/jetpack/compose/m3-typography.png",
                                articleId = 0
                            ),
                            description = "The Text composable has multiple optional parameters to style its content. Below, we’ve listed parameters that cover the most common use cases with text. For all the parameters of Text, see the Compose Text source code."
                        ),
                        pubTime = "22 MARCH 2024",
                        modifier = Modifier,
                        onPlayAudio = {},)
                }
            }

        }
    }
}