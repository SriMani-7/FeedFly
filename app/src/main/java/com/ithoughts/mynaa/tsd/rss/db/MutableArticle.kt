package com.ithoughts.mynaa.tsd.rss.db

data class MutableArticle(
    private var feedId: Long
) {
    var title: String = ""
    var link: String = ""
    var category: String =""
    var description: String? = null

    fun immutable() = ArticleItem(
        title = title,
        link = link,
        category = category,
        description = description,
        feedId = feedId
    )
}