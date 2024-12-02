package srimani7.apps.feedfly.core.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.dao.FeedDao
import srimani7.apps.feedfly.core.database.entity.Label
import javax.inject.Inject

internal class LabelRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao,
    private val feedDao: FeedDao,
): LabelRepository {

    override fun getAllLabels() = articleDao.getLabels()

    override suspend fun updateArticleLabel(articleId: Long, labelId: Long) = articleDao.updateLabel(articleId, labelId)

    override suspend fun removeArticleLabel(articleId: Long) = articleDao.removeArticleLabel(articleId)

    override suspend fun addLabel(it: String) {
        articleDao.addLabel(Label(it, false))
    }

    override fun getArticleLabels(feedId: Long) = feedDao.getArticleLabels(feedId)
    override fun getLabel(id: Long) = articleDao.getLabel(id)
    override fun getArticles(labelId: Long) = articleDao.getArticles(labelId)
    override fun getPinnedLabels() = articleDao.getPinnedLabels()

    override fun getArticlesForGroup(group: String?) = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            if (group != null) articleDao.getArticlesForGroup(group)
            else articleDao.getArticlesForAllGroups()
        }
    ).flow
}