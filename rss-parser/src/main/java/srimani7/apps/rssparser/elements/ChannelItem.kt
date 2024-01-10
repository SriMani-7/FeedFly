package srimani7.apps.rssparser.elements

data class ChannelItem(
    var author: String? = null,
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var categories: List<String> = emptyList(),
    var enclosure: ItemEnclosure? = null
)