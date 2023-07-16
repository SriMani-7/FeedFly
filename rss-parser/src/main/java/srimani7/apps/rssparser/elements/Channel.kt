package srimani7.apps.rssparser.elements

data class Channel(
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var lastBuildDate: String? = null,
    var language: String? = null,
    var managingEditor: String? = null,
    var copyright: String? = null,
    var image: ChannelImage? = null,
    var items: List<ChannelItem> = emptyList()
)