package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.LabelledArticle

interface ReadLaterRepository {
    suspend fun markArticle(articleId: Long)
    suspend fun unMarkArticle(articleId: Long)
    fun getAllArticles(): Flow<List<LabelledArticle>>
    fun getCount(): Flow<Long>
}