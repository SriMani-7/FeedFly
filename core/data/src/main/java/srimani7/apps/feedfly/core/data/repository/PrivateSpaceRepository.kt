package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.PrivateArticle

interface PrivateSpaceRepository {
    val groups: Flow<List<String>>
    fun getPrivateArticles(group: String): Flow<List<PrivateArticle>>
    suspend fun unLockArticle(it: Long)
    suspend fun moveArticleToPrivate(l: Long)
}