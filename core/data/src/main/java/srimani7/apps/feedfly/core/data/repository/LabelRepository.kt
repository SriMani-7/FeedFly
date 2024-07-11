package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.LabelData

interface LabelRepository {
    fun getAllLabels(): Flow<List<LabelData>>

    suspend fun addLabel(it: String)
    suspend fun removeArticleLabel(articleId: Long)
    suspend fun updateArticleLabel(articleId: Long, labelId: Long)
    fun getArticleLabels(feedId: Long): Flow<List<LabelData>>
}