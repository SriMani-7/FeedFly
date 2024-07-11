package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.LabelData
import srimani7.apps.feedfly.core.model.LabelModel
import srimani7.apps.feedfly.core.model.LabelledArticle

interface LabelRepository {
    fun getAllLabels(): Flow<List<LabelData>>
    fun getLabel(id: Long): Flow<LabelModel?>
    fun getArticles(labelId: Long): Flow<List<LabelledArticle>>

    suspend fun updateArticleLabel(articleId: Long, labelId: Long)
    suspend fun removeArticleLabel(articleId: Long)
    suspend fun addLabel(it: String)
}