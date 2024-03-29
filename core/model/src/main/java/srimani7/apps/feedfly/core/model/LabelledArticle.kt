package srimani7.apps.feedfly.core.model

import java.util.Date

data class LabelledArticle(
    val articleId: Long,
    val title: String,
    val description: String?,
    val articleLink: String,
    val publishedTime: Date?,
    val mediaType: String?,
    val mediaSrc: String?,
    val label: String?,
    val labelId: Long?
) {
    val isImage get() = mediaType?.contains("image") ?: false
}
