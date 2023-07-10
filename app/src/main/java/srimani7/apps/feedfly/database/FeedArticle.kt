package srimani7.apps.feedfly.database

import androidx.room.ColumnInfo
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed
import java.util.Date

class FeedArticle(val feed: Feed, val articles: List<ArticleItem>)

class FavoriteArticle(
    val title: String,
    val link: String,
    val category: String,
    @ColumnInfo("pinned") val pinned: Boolean = false,
    @ColumnInfo("pub_date") val pubDate: Date? = null,
    @ColumnInfo("article_id") val id: Long,
    @ColumnInfo("feed_title") val feedTitle: String,
    val description: String? = null,
)