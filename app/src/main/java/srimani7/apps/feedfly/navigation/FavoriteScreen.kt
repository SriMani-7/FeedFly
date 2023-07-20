@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import srimani7.apps.feedfly.database.FavoriteArticle
import srimani7.apps.feedfly.ui.ArticleFavoriteToggle
import srimani7.apps.feedfly.ui.DescriptionText
import srimani7.apps.feedfly.ui.ShowImageDialog
import srimani7.apps.feedfly.viewmodel.HomeViewModal
import srimani7.apps.feedfly.viewmodel.RssViewModal
import srimani7.apps.rssparser.DateParser

@Composable
fun FavoriteScreen(homeViewModal: HomeViewModal) {
    val articleGroups by homeViewModal.favoriteArticles.collectAsState(null)
    var currentGroup by remember { mutableStateOf(articleGroups?.keys?.first()) }
    var articles by rememberSaveable(currentGroup) {
        mutableStateOf(articleGroups?.get(currentGroup))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            articleGroups?.let { articleGroups ->
                LazyRow(
                    contentPadding = PaddingValues(16.dp, 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    articleGroups.keys.forEach {
                        item(it) {
                            FilterChip(
                                selected = currentGroup == it,
                                onClick = {
                                    currentGroup = it
                                },
                                label = { Text(text = it ?: "Others") }
                            )
                        }
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 30.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    articles?.forEach {
                        item(it.id) {
                            FavoriteArticleCard(it, onPinChange = homeViewModal::updateArticle)
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(articleGroups) {
        articles = articleGroups?.get(currentGroup)
        if (articles == null || articles!!.isEmpty()) {
            currentGroup = articleGroups?.keys?.firstOrNull()
        }
    }
}

@Composable
fun FavoriteArticleCard(item: FavoriteArticle, onPinChange: (Long, Boolean) -> Unit) {
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
                            .defaultMinSize(minHeight = 100.dp),
                        filterQuality = FilterQuality.Medium,
                    )
                }
            }
            Text(
                text = item.feedTitle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        12.dp,
                        top = 16.dp,
                        end = 12.dp
                    )
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 10.dp)
            )
            item.description?.let {
                DescriptionText(
                    description = it,
                    modifier = Modifier.padding(12.dp, 8.dp)
                ) { src ->
                    imageSrc = src
                    RssViewModal.info(src)
                    ColorDrawable(Color.GRAY)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    if (item.category.isNotBlank())
                        Text(text = item.category, style = MaterialTheme.typography.labelMedium)
                    DateParser.formatDate(item.pubDate)
                        ?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
                }
                Spacer(modifier = Modifier.weight(1f))
                ArticleFavoriteToggle(item.pinned) { onPinChange(item.id, it) }
            }
            Divider(thickness = 1.5.dp)
        }
    }
    if (showImage && imageSrc != null)
        ShowImageDialog(imageSrc!!) {
            showImage = false
        }
}