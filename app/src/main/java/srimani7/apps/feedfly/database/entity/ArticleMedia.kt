package srimani7.apps.feedfly.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("articles_media", foreignKeys = [
    ForeignKey(
        ArticleItem::class,
        parentColumns = ["article_id"],
        childColumns = ["article_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
], indices = [
    Index(value = ["article_id"], unique = true)
])
data class ArticleMedia(
    @ColumnInfo("media_size") val length: Long?,
    @ColumnInfo("mime_type") val type: String?,
    @ColumnInfo("url") val url: String?,
    @ColumnInfo("article_id") val articleId: Long,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Long = 0
)