@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.ui.RssItemCard
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun FavoriteScreen(homeViewModal: HomeViewModal) {
    val articleGroups by homeViewModal.favoriteArticles.collectAsState(null)
    var currentGroup by remember { mutableStateOf(articleGroups?.keys?.first()) }
    var articles by remember(currentGroup) {
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
                    contentPadding = PaddingValues(
                        bottom = 80.dp, top = 8.dp,
                        start = 8.dp, end = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    articles?.forEach { article ->
                        item(article.id) {
                            RssItemCard(
                                article,
                                onPlayAudio = {},
                                onPinChange = { homeViewModal.updateArticle(article.id, it) })
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