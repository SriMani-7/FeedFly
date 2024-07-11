package srimani7.apps.feedfly.core.data.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import srimani7.apps.feedfly.core.data.asArticleItem
import srimani7.apps.feedfly.core.data.asFeed
import srimani7.apps.feedfly.core.data.asFeedImage
import srimani7.apps.feedfly.core.data.model.FeedFetchState
import srimani7.apps.feedfly.core.data.repository.RssFeedRepository
import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.dao.FeedDao
import srimani7.apps.feedfly.core.database.dao.dbErrorLog
import srimani7.apps.feedfly.core.database.dao.dbInfoLog
import srimani7.apps.feedfly.core.model.FeedModel
import srimani7.apps.rssparser.OkHttpWebService
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.RssParser
import srimani7.apps.rssparser.elements.Channel
import java.util.Date
import javax.inject.Inject

class RssFeedRepositoryImpl @Inject constructor(
    private val feedDao: FeedDao,
    private val articleDao: ArticleDao
): RssFeedRepository {
    override fun getFeed(feedId: Long) = feedDao.getFeed(feedId)
    override suspend fun deleteFeed(id: Long) = feedDao.delete(id)
    override suspend fun deleteArticle(articleId: Long) = articleDao.deleteArticle(articleId)
    override fun getFeedArticles(feedId: Long, id: Long?) =
        feedDao.getFeedArticles(feedId, id ?: -1, id == null)

    override suspend fun updateFeedGroup(id: Long, name: String) = feedDao.updateFeedGroup(id, name)

    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }

    private var buildData: Date? = null
    override val uiState = MutableStateFlow<FeedFetchState?>(null)

    override suspend fun updateFeed(feed: FeedModel) {
        try {
            uiState.update { FeedFetchState.Loading }
            val streamResult = okHttpWebService.inputStreamResult(feed.feedUrl)
            val state = streamResult.getOrThrow().let {
                rssParser.parse(it, feed.lastBuildDate, feed.feedUrl)
            }
            when (state) {
                ParsingState.Completed -> {}
                is ParsingState.Failure -> {
                    state.exception.printStackTrace()
                    uiState.update { FeedFetchState.Failure(state.exception.message) }
                }

                ParsingState.LastBuild -> {
                    buildData = feed.lastBuildDate
                    uiState.value = FeedFetchState.LastBuild
                }

                ParsingState.Processing -> uiState.update { FeedFetchState.Loading }
                is ParsingState.Success -> {
                    if (state.channel.lastBuildDate == null) {
                        buildData = Date()
                        state.channel.lastBuildDate = feed.lastBuildDate
                    } else buildData = state.channel.lastBuildDate
                    updateAndInsertArticles(feed, state.channel)
                    uiState.value = FeedFetchState.Completed
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uiState.update { FeedFetchState.Failure(e.message) }
        }
    }

    private suspend fun updateAndInsertArticles(feed: FeedModel, channel: Channel) = withContext(
        Dispatchers.IO) {
        feedDao.updateFeedUrl(feed.asFeed(channel))
        channel.image?.let {
            launch {
                feedDao.insert(it.asFeedImage(feed.id))
                cancel()
            }
        }
        channel.items.forEach { channelItem ->
            val article = channelItem.asArticleItem(feed.id)
            val rowId = feedDao.insertArticle(
                title = article.title,
                link = article.link,
                category = article.category,
                feedId = article.feedId,
                lastFetch = article.lastFetched, pubDate = article.pubDate,
                description = article.description, author = article.author
            )
            val enclosure = channelItem.enclosure
            if (enclosure != null && rowId != -1L) launch {
                try {
                    feedDao.insertArticleMedia(
                        mediaSize = enclosure.length,
                        mediaType = enclosure.type,
                        url = enclosure.url,
                        articleLink = article.link,
                        articleTitle = article.title
                    )
                    dbInfoLog("Inserted media")
                } catch (e: Exception) {
                    dbErrorLog("For the $enclosure ${article.title} ${article.link}", e)
                } finally {
                    cancel()
                }
            }
        }
    }
}