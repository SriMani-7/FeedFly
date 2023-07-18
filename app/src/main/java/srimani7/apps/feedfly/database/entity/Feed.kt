package srimani7.apps.feedfly.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import srimani7.apps.rssparser.elements.Channel
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
    @ColumnInfo("group_name") val group: String? = null,
    @ColumnInfo("language_code") val language: String? = null,
    @ColumnInfo("managing_editor_email") val managingEditor: String? = null,
    @ColumnInfo("copyright") val copyright: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    constructor(map: Map<String, Any>) : this(
        feedUrl = (map["feedUrl"] ?: "").toString(),
        description = (map["description"] ?: "").toString(),
        link = (map["link"] ?: "").toString(),
        title = (map["title"] ?: "").toString(),
        lastBuildDate = map["lastBuildDate"] as Date?
    )

    fun copy(channel: Channel) = copy(
        description = channel.description,
        link = channel.link ?: "",
        title = channel.title ?: "",
        lastBuildDate = channel.lastBuildDate ?: Date(),
        language = channel.language,
        managingEditor = channel.managingEditor,
        copyright = channel.copyright
    )

    companion object {
        fun tags() = arrayOf("link", "description", "title", "feedUrl")
    }
}