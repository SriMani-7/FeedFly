package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.model.SimpleFeed
import srimani7.apps.rssparser.DateParser

@Composable
fun FeedGroupList(
    groups: List<SimpleFeed>,
    state: LazyListState = rememberLazyListState(),
    onClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxSize(),
        state = state
    ) {
        items(groups, key = {it.id}) { feedDto ->
            FeedCard(feedDto) { onClick(feedDto.id) }
        }
    }
}

@Composable
fun FeedCard(feedDto: SimpleFeed, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(14.dp, 14.dp),
            ) {
                AsyncImage(
                    model = feedDto.imageUrl,
                    contentDescription = "image",
                    contentScale = ContentScale.FillHeight,
                    filterQuality = FilterQuality.Medium,
                    alignment = Alignment.CenterStart,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.LightGray, CircleShape)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.rss_feed_24px),
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = feedDto.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                    )
                    DateParser.formatDate(feedDto.lastBuildDate)?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
            }
        }
    }
}