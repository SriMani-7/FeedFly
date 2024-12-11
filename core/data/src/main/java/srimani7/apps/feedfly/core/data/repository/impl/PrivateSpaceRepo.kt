package srimani7.apps.feedfly.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.data.repository.PrivateSpaceRepository
import srimani7.apps.feedfly.core.database.dao.PrivateSpaceDao
import srimani7.apps.feedfly.core.model.PrivateArticle
import javax.inject.Inject

internal class PrivateSpaceRepo @Inject constructor(
    private val dao: PrivateSpaceDao
): PrivateSpaceRepository {
    override val groups: Flow<List<String>> get() = dao.getGroups()
    override fun getPrivateArticles(group: String): Flow<List<PrivateArticle>> = dao.getPrivateArticles(group)
    override suspend fun unLockArticle(it: Long) = dao.unLockArticle(it)
    override suspend fun moveArticleToPrivate(l: Long) = dao.moveArticleToPrivate(l)
}