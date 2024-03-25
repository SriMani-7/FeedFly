package srimani7.apps.feedfly.ui.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.database.dto.FeedArticle
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.feedfly.navigation.ArticleViewScreen
import srimani7.apps.feedfly.ui.HtmlImage
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssItemCard(
    item: FeedArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(item.pubDate) ?: "",
    onPlayAudio: (String) -> Unit,
    onPinChange: (Boolean) -> Unit
) {
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    OutlinedCard(
        onClick = { scope.launch { articleModalState.show() } },
        shape = MaterialTheme.shapes.medium,
        border = CardDefaults.outlinedCardBorder().copy(.4.dp),
        modifier = modifier
    ) {
        item.description?.let {
            HtmlImage(it) { src ->
                descriptionUri = src
                null
            }
            if (descriptionUri != null && item.articleMedia?.urlType != ArticleMedia.MediaType.IMAGE)
                ArticleImage(descriptionUri!!)
        }
        item.articleMedia?.let { ArticleMediaHeader(it, onPlayAudio) }
        Column(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 10.dp)
                .fillMaxWidth(),
        ) {
            if (item.title.isNotBlank()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (item.category.isNotBlank())
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Text(
                text = pubTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.weight(1f))
            ArticleFavoriteToggle(item.pinned) { onPinChange(it) }
        }
    }
    if (articleModalState.isVisible) {
        ArticleViewScreen(item, articleModalState, onPinChange) {
            scope.launch { articleModalState.hide() }
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
    val pubTime by remember {
        mutableStateOf(
            DateParser.parseDate(item.pubDate)
                ?.let { SimpleDateFormat.getDateTimeInstance().format(it) }
                ?: ""
        )
    }
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    OutlinedCard(
        onClick = { scope.launch { articleModalState.show() } },
        shape = MaterialTheme.shapes.medium,
        border = CardDefaults.outlinedCardBorder().copy(.4.dp),
        modifier = modifier
    ) {
        item.description?.let {
            HtmlImage(it) { src ->
                descriptionUri = src
                null
            }
            if (descriptionUri != null && ArticleMedia.isImage(item.enclosure?.type))
                ArticleImage(descriptionUri!!)
        }
        item.enclosure?.let {
            ArticleMediaHeader(ArticleMedia(it, 1), onPlayAudio)
        }
        Column(
            modifier = Modifier
                .padding(14.dp, 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.title?.isNotBlank() == true) {
                Text(
                    text = item.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = item.categories.joinToString(","),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pubTime,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        }
    }
    if (articleModalState.isVisible) {
        ArticleViewScreen(item.description, item.link ?: "", articleModalState) {
            scope.launch { articleModalState.hide() }
        }
    }
}