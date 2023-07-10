package srimani7.apps.feedfly.database

import androidx.room.ColumnInfo
import androidx.room.Relation
import srimani7.apps.feedfly.database.entity.Feed

data class FeedGroup(
    @ColumnInfo("name")
    val name: String?,
    @Relation(
        entityColumn = "group_name",
        parentColumn = "name"
    ) val feeds: List<Feed>
)