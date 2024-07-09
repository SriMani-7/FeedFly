package srimani7.apps.feedfly.core.data

import android.app.Application
import srimani7.apps.feedfly.core.database.AppDatabase
import srimani7.apps.feedfly.core.database.entity.Label

class LabelRepository(application: Application) {
    private val articleDao by lazy { AppDatabase.getInstance(application).articleDao() }

    fun getAllLabels() = articleDao.getLabels()

    suspend fun updateArticleLabel(articleId: Long, labelId: Long) = articleDao.updateLabel(articleId, labelId)

    suspend fun removeArticleLabel(articleId: Long) = articleDao.removeArticleLabel(articleId)

    suspend fun addLabel(it: String) {
        articleDao.addLabel(Label(it, false))
    }

    fun getLabel(id: Long) = articleDao.getLabel(id)
    fun getArticles(labelId: Long) = articleDao.getArticles(labelId)
}