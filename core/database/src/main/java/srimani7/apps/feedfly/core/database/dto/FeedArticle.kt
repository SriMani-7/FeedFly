package srimani7.apps.feedfly.core.database.dto

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Relation
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import java.util.Date

class FeedArticle(
    val title: String,
    val link: String,
    val category: String,
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
)
