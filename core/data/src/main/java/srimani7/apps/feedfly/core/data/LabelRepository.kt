package srimani7.apps.feedfly.core.data

import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.entity.Label
import javax.inject.Inject

class LabelRepository @Inject constructor(
    private val articleDao: ArticleDao
) {

    fun getAllLabels() = articleDao.getLabels()

    suspend fun updateArticleLabel(articleId: Long, labelId: Long) = articleDao.updateLabel(articleId, labelId)

    suspend fun removeArticleLabel(articleId: Long) = articleDao.removeArticleLabel(articleId)

    suspend fun addLabel(it: String) {
        articleDao.addLabel(Label(it, false))
    }

}