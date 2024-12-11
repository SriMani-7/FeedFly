package srimani7.apps.feedfly.core.model

import java.util.Date

data class FeedModel(
    val feedUrl: String,
    val description: String?,
    val link: String,
    val title: String,
    val lastBuildDate: Date? = null,
    val groupName: String = "Others",
    val id: Long = 0
)
