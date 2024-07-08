package srimani7.apps.feedfly.core.model

import java.util.Date

data class SimpleFeed(
    val id: Long,
    val feedUrl: String,
    val link: String,
    val title: String,
    val imageUrl: String?,
    val lastBuildDate: Date? = null,
)
