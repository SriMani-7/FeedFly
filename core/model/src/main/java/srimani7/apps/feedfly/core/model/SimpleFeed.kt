package srimani7.apps.feedfly.core.model

import java.util.Date

data class SimpleFeed(
    val id: Long,
    val feedUrl: String,
    val title: String,
    val lastBuildDate: Date? = null,
    val groupName: String? = null,
    val imageUrl: String? = null
)
