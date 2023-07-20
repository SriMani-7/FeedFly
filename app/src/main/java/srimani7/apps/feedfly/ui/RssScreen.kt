@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.ui

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import srimani7.apps.feedfly.database.FeedArticle
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.rssparser.DateParser

@Composable
fun RssItemsColumn(
    dateListMap: Map<String?, List<FeedArticle>>,
    updateArticle: (Long, Boolean) -> Unit
) {
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(10.dp, 15.dp)
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.id }
                ) { feedArticle ->
                    RssItemCard(feedArticle) {
                        updateArticle(feedArticle.id, it)
                    }
                }
            }
        }
    }
}

@Composable
fun RssItemCard(item: FeedArticle, onPinChange: (Boolean) -> Unit) {
    val context = LocalContext.current

    OutlinedCard(
        onClick = {
            val intent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .build().apply {
                    intent.putExtra(
                        "com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB",
                        true
                    )
                }
            intent.launchUrl(context, Uri.parse(item.link))
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        item.articleMedia?.let { ArticleImage(it, item.description) }
        Column(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.category.isNotBlank())
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelLarge,
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
            DateParser.formatTime(item.pubDate)
                ?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light
                    )
                }
            Spacer(modifier = Modifier.weight(1f))
            ArticleFavoriteToggle(item.pinned) { onPinChange(it) }
        }
    }
}

@Composable
fun ArticleImage(articleMedia: ArticleMedia, description: String?) {
    var imageSrc by rememberSaveable { mutableStateOf(articleMedia.url) }
    var mediaType by rememberSaveable { mutableStateOf(articleMedia.urlType) }
    when (mediaType) {
        ArticleMedia.MediaType.IMAGE -> AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageSrc)
                .build(),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 140.dp),
            filterQuality = FilterQuality.Medium,
        )

        else -> {}
    }

    description?.let {
        DescriptionText(it, modifier = Modifier.padding(12.dp, 8.dp)) { src ->
            if (imageSrc != null) {
                imageSrc = src; mediaType = ArticleMedia.MediaType.IMAGE
            }
            null
        }
    }
}