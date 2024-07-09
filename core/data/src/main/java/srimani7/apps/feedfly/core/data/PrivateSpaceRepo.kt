package srimani7.apps.feedfly.core.data

import android.app.Application
import srimani7.apps.feedfly.core.database.AppDatabase

class PrivateSpaceRepo(application: Application) {
    private val dao by lazy { AppDatabase.getInstance(application).privateSpaceDao() }

    val groups get() = dao.getGroups()
    fun getPrivateArticles(group: String) = dao.getPrivateArticles(group)
    suspend fun unLockArticle(it: Long) = dao.unLockArticle(it)
}