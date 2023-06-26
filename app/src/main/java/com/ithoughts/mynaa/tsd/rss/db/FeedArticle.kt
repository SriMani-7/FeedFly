package com.ithoughts.mynaa.tsd.rss.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "feeds", indices = [
    Index(value = ["feed_url"], unique = true)
])
data class Feed(
    @ColumnInfo("feed_url") val feedUrl: String,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("link") val link: String,
    @ColumnInfo("feed_title") val title: String,
    @ColumnInfo("last_build_date") val lastBuildDate: Date? = null,
    @ColumnInfo("group_name") val group: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    constructor(map: Map<String, Any>) : this(
        feedUrl = (map["feedUrl"] ?: "").toString(),
        description = (map["description"] ?: "").toString(),
        link = (map["link"] ?: "").toString(),
        title = (map["title"] ?: "").toString(),
        lastBuildDate = map["lastBuildDate"] as Date?
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
    ], indices = [
        Index(value = ["title"], unique = true)
    ]
)
data class ArticleItem(
    val title: String,
    val link: String,
    val category: String,
    @ColumnInfo("feed_id") val feedId: Long,
    @ColumnInfo("pinned") val pinned: Boolean = false,
    @ColumnInfo("lastFetch") val lastFetched: Date? = null,
    @ColumnInfo("pub_date") val pubDate: Date? = null,
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