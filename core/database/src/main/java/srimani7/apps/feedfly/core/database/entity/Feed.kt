package srimani7.apps.feedfly.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "feeds", indices = [
        Index(value = ["feed_url"], unique = true)
    ]
)
data class Feed(
    @ColumnInfo("feed_url") val feedUrl: String,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("link") val link: String,
    @ColumnInfo("feed_title") val title: String,
    @ColumnInfo("last_build_date") val lastBuildDate: Date? = null,
    @ColumnInfo("group_name", defaultValue = "Others") val group: String = "Others",
    @ColumnInfo("language_code") val language: String? = null,
    @ColumnInfo("managing_editor_email") val managingEditor: String? = null,
    @ColumnInfo("copyright") val copyright: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)