package srimani7.apps.feedfly.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Relation
import srimani7.apps.feedfly.database.entity.FeedImage
import java.util.Date

class FeedDto(
    @ColumnInfo("feed_url") val feedUrl: String,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("feed_title") val title: String,
    @ColumnInfo("last_build_date") val lastBuildDate: Date? = null,
    @ColumnInfo("group_name") val group: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long,

    @Relation(
        entityColumn = "feed_id",
        parentColumn = "id",
        entity = FeedImage::class
    )
    val feedImageDto: FeedImageDto?
) {
    data class FeedImageDto(
        @ColumnInfo("image_url") val imageUrl: String,
        @ColumnInfo("feed_id") val feedId: Long,
        @ColumnInfo("id") val imageId: Long
    )
}