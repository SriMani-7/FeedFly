package srimani7.apps.feedfly.core.data.repository.impl

import srimani7.apps.feedfly.core.data.repository.ReadLaterRepository
import srimani7.apps.feedfly.core.database.dao.ReadLaterDao
import javax.inject.Inject

internal class ReadLaterRepositoryImpl @Inject constructor(
    private val readLaterDao: ReadLaterDao
) : ReadLaterRepository {
    override suspend fun markArticle(articleId: Long) {
        readLaterDao.markArticle(articleId)
    }

    override suspend fun unMarkArticle(articleId: Long) {
        readLaterDao.unMarkArticle(articleId)
    }

    override fun getAllArticles() = readLaterDao.getArticles()
    override fun getCount() = readLaterDao.countArticles()

}