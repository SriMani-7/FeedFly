package srimani7.apps.feedfly.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Relation
import srimani7.apps.feedfly.database.entity.ArticleMedia
import java.util.Date

class FeedArticle(
    val title: String,
    val link: String,
    val category: String,
    @ColumnInfo("pinned") val pinned: Boolean = false,
    @ColumnInfo("pub_date") val pubDate: Date? = null,
    val description: String? = null,
    @ColumnInfo("author") val author: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("article_id") val id: Long = 0,
    @Relation(
        parentColumn = "article_id",
        entityColumn = "article_id"
    )
    val articleMedia: ArticleMedia?
) {

}

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