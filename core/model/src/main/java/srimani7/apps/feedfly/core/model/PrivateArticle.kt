package srimani7.apps.feedfly.core.model

import java.util.Date

data class PrivateArticle(
    val articleId: Long,
    val title: String,
    val description: String?,
    val articleLink: String,
    val publishedTime: Date?,
    val mediaType: String?,
    val mediaSrc: String?,
    val label: String?,
    val feedTitle: String,
    val feedId: Long
) {
    val isImage get() = mediaType?.contains("image") ?: false
}