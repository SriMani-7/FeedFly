package srimani7.apps.feedfly.database

import srimani7.apps.feedfly.database.entity.ArticleItem
import java.util.Date

data class MutableArticle(
    private var feedId: Long
) {
    var title: String = ""
    var link: String = ""
    var category: String = ""
    var description: String? = null
    var lastFetched: Date? = null
    var pubDate: Date? = null

    fun immutable() = ArticleItem(
        title = title,
        link = link,
        category = category,
        description = description,
        feedId = feedId,
        lastFetched = lastFetched,
        pubDate = pubDate
    )
}