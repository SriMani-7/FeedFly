package srimani7.apps.feedfly.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "feed_images", foreignKeys = [
        ForeignKey(
            Feed::class,
            parentColumns = ["id"],
            childColumns = ["feed_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ], indices = [
        Index(value = ["feed_id"], unique = true)
    ]
)
data class FeedImage(
    @ColumnInfo("website_link") val link: String,
    @ColumnInfo("image_title") val title: String,
    @ColumnInfo("image_url") val url: String,
    @ColumnInfo("feed_id") val feedId: Long,
    @ColumnInfo("description") val description: String? = null,
    @ColumnInfo("image_height") val height: Int = DEFAULT_WIDTH,
    @ColumnInfo("image_width") val width: Int = DEFAULT_HEIGHT,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Long = 0
) {

    companion object {
        const val DEFAULT_WIDTH = 88
        const val DEFAULT_HEIGHT = 31
        const val MAX_WIDTH = 144
        const val MAX_HEIGHT = 400
    }
}