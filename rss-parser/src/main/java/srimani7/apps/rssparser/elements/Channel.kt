package srimani7.apps.rssparser.elements

import java.util.Date

data class Channel(
    val feedUrl: String,
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var lastBuildDate: Date? = null,
    var language: String? = null,
    var managingEditor: String? = null,
    var copyright: String? = null,
    var image: ChannelImage? = null,
    var items: List<ChannelItem> = emptyList()
)