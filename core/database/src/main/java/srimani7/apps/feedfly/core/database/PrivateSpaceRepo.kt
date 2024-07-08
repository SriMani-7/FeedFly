package srimani7.apps.feedfly.core.database

import android.app.Application

class PrivateSpaceRepo(application: Application) {
    private val dao by lazy { AppDatabase.getInstance(application).privateSpaceDao() }

    val groups get() = dao.getGroups()
    fun getPrivateArticles(group: String) = dao.getPrivateArticles(group)
    suspend fun unLockArticle(it: Long) = dao.unLockArticle(it)
}