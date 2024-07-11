package srimani7.apps.feedfly.core.data.repository.impl

import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.entity.Label
import javax.inject.Inject

internal class LabelRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao
): LabelRepository {

    override fun getAllLabels() = articleDao.getLabels()

    override suspend fun updateArticleLabel(articleId: Long, labelId: Long) = articleDao.updateLabel(articleId, labelId)

    override suspend fun removeArticleLabel(articleId: Long) = articleDao.removeArticleLabel(articleId)

    override suspend fun addLabel(it: String) {
        articleDao.addLabel(Label(it, false))
    }

}