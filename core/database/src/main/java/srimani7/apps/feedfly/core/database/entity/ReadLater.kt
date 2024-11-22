package srimani7.apps.feedfly.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "read_later_articles",
    foreignKeys = [
        ForeignKey(
            ArticleItem::class,
            parentColumns = ["article_id"],
            childColumns = ["article_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ], indices = [Index("article_id", unique = true)]
)
data class ReadLater(
    @ColumnInfo("article_id") val articleId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)