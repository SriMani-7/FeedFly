package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import srimani7.apps.feedfly.core.data.model.FeedFetchState
import srimani7.apps.feedfly.core.model.FeedModel
import srimani7.apps.feedfly.core.model.LabelledArticle

interface RssFeedRepository {
    fun getFeed(feedId: Long): Flow<FeedModel>
    fun getFeedArticles(feedId: Long, id: Long?): Flow<List<LabelledArticle>>
    val uiState: StateFlow<FeedFetchState?>

    suspend fun deleteFeed(id: Long)
    suspend fun deleteArticle(articleId: Long)
    suspend fun updateFeedGroup(id: Long, name: String)
    suspend fun updateFeed(feed: FeedModel)
}