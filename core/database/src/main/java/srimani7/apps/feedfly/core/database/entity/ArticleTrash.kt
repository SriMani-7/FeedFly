package srimani7.apps.feedfly.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "articles_trash", foreignKeys = [
        ForeignKey(
            Feed::class,
            parentColumns = ["id"],
            childColumns = ["feed_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ], indices = [
        Index(value = ["feed_id", "title", "link"], unique = true)
    ]
)
class ArticleTrash(
    val title: String,
    val link: String,
    @ColumnInfo("feed_id") val feedId: Long,
    @ColumnInfo("last_delete") val lastDelete: Date? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("trash_id") val id: Long = 0
)