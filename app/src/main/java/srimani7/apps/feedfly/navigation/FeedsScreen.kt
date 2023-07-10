@file:OptIn(ExperimentalMaterial3Api::class)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.rss.DateParser

@Composable
fun FeedGroupList(groupName: String, feeds: List<Feed>, onClick: (Long) -> Unit) {
    Column {
        Text(text = groupName, modifier = Modifier.padding(start = 16.dp))
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(16.dp, 16.dp),
        ) {
            items(feeds, key = { it.id }) { feed ->
                FeedCard(feed = feed, onClick = { onClick(feed.id) })
            }
        }
    }
}

@Composable
fun FeedCard(feed: Feed, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.size(145.dp, 135.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            DateParser.formatDate(feed.lastBuildDate)?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(0.dp, 4.dp)
                )
            }
            Text(
                text = feed.title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}