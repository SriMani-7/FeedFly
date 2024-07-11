package srimani7.apps.feedfly.core.data

import srimani7.apps.feedfly.core.database.dao.PrivateSpaceDao
import javax.inject.Inject

class PrivateSpaceRepo @Inject constructor(
    private val dao: PrivateSpaceDao
) {
    val groups get() = dao.getGroups()
    fun getPrivateArticles(group: String) = dao.getPrivateArticles(group)
    suspend fun unLockArticle(it: Long) = dao.unLockArticle(it)
}