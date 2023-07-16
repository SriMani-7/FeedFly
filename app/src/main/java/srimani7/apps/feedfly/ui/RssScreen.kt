@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package srimani7.apps.feedfly.ui

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.rssparser.DateParser
import srimani7.apps.feedfly.viewmodel.RssViewModal

@Composable
fun RssItemsColumn(
    dateListMap: Map<String?, List<ArticleItem>>,
    updateArticle: (ArticleItem) -> Unit
) {
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(vertical = 15.dp)
        ) {
            dateListMap.forEach { entry ->
                entry.key?.let { date ->
                    stickyHeader {
                        ArticleHeader(date)
                    }
                }
                items(entry.value,
                    key = { it.id }
                ) {
                    RssItemCard(it, updateArticle)
                }
            }
        }
    }
}

@Composable
fun RssItemCard(item: ArticleItem, onPinChange: (ArticleItem) -> Unit) {
    var imageSrc by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var showImage by remember { mutableStateOf(false) }

    Surface(
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
        shape = MaterialTheme.shapes.small,
    ) {
        Column {
            Box {
                imageSrc?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageSrc)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.baseline_photo_size_select_actual_24),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showImage = true }
                            .defaultMinSize(minHeight = 150.dp),
                        filterQuality = FilterQuality.Medium,
                    )
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .then(
                            if (imageSrc == null) Modifier.padding(
                                12.dp,
                                top = 16.dp,
                                bottom = 10.dp,
                                end = 12.dp
                            )
                            else Modifier
                                .background(
                                    MaterialTheme.colorScheme
                                        .surfaceColorAtElevation(6.dp)
                                        .copy(alpha = 0.8f)
                                )
                                .padding(12.dp, 12.dp)
                        )
                )
            }
            item.description?.let {
                DescriptionText(it, modifier = Modifier.padding(12.dp, 8.dp)) { src ->
                    imageSrc = src
                    RssViewModal.info(src)
                    ColorDrawable(android.graphics.Color.GRAY)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    if (item.category.isNotBlank())
                        Text(text = item.category, style = MaterialTheme.typography.labelMedium)
                    DateParser.formatTime(item.pubDate)
                        ?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
                }
                Spacer(modifier = Modifier.weight(1f))
                ArticleFavoriteToggle(item.pinned) { onPinChange(item.copy(pinned = it)) }
            }
            Divider(thickness = 1.5.dp)
        }
    }
    if (showImage && imageSrc != null)
        ShowImageDialog(imageSrc!!) {
            showImage = false
        }
}