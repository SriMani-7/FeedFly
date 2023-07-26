package srimani7.apps.feedfly.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.ChannelItem
import java.util.Date

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
        Index(value = ["title", "link"], unique = true)
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
    @ColumnInfo("author") val author: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("article_id") val id: Long = 0
) {
    constructor(channelItem: ChannelItem, feedId: Long) : this(
        title = channelItem.title ?: "",
        link = channelItem.link ?: "",
        category = channelItem.categories.joinToString(separator = ", "),
        lastFetched = Date(),
        pubDate = DateParser.parseDate(channelItem.pubDate),
        description = channelItem.description,
        author = channelItem.author,
        feedId = feedId
    )
}