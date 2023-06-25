package com.ithoughts.mynaa.tsd.rss.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "feeds")
data class Feed(
    @ColumnInfo("feed_url") val feedUrl: String,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("link") val link: String,
    @ColumnInfo("feed_title") val title: String,
    @ColumnInfo("last_build_date") val lastBuildDate: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    constructor(map: Map<String, String>) : this(
        map["feedUrl"] ?: "",
        map["description"],
        map["link"] ?: "",
        map["title"] ?: "",
        map["lastBuildDate"]
    )

    companion object {
        fun tags() = arrayOf("link", "description", "title", "feedUrl")
    }
}

@Entity(
    tableName = "articles", foreignKeys = [
        ForeignKey(
            Feed::class,
            parentColumns = ["id"],
            childColumns = ["feed_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ArticleItem(
    val title: String,
    val link: String,
    val category: String,
    @ColumnInfo("feed_id") val feedId: Long,
    val description: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("article_id") val id: Long = 0
)

data class FeedArticle(
    @Embedded val feed: Feed,
    @Relation(
        parentColumn = "id",
        entityColumn = "feed_id"
    )
    val articles: List<ArticleItem>
)