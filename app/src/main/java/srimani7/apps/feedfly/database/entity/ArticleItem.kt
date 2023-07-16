package srimani7.apps.feedfly.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    @ColumnInfo("author") val author: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("article_id") val id: Long = 0
)