package srimani7.apps.feedfly.core.database

import android.app.Application
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import java.util.Date

class Repository(application: Application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    fun getArticles(feedId: Long) = feedDao.getArticles(feedId)
    fun getFeed(feedId: Long) = feedDao.getFeed(feedId)
    fun getGroups() = feedDao.getGroups()
    fun getAllFeeds() = feedDao.getAllFeeds()
    fun getFavoriteFeedArticles() = feedDao.getFavoriteFeedArticles()

    suspend fun updateFeedUrl(copy: Feed) {
        feedDao.updateFeedUrl(copy)
    }

    suspend fun delete(feed: Feed) {
        feedDao.delete(feed)
    }

    suspend fun insertArticleMedia(
        mediaSize: Long?,
        mediaType: String?,
        url: String?,
        articleLink: String,
        articleTitle: String
    ) {
        feedDao.insertArticleMedia(mediaSize, mediaType, url, articleLink, articleTitle)
    }

    suspend fun insertArticle(
        title: String,
        link: String,
        category: String,
        feedId: Long,
        lastFetch: Date?,
        pubDate: Date?,
        description: String?,
        author: String?
    ) = feedDao.insertArticle(
        title,
        link,
        category,
        feedId,
        lastFetch,
        pubDate,
        description,
        author
    )

    suspend fun insert(feedImage: FeedImage) {
        feedDao.insert(feedImage)
    }

    suspend fun updateArticlePin(id: Long, pinned: Boolean) {
        feedDao.updateArticlePin(id, pinned)
    }

    suspend fun insertFeedUrl(feed: Feed) {
        feedDao.insertFeedUrl(feed)
    }

    suspend fun removeOldArticles(feedId: Long, threshold: Long) {
        feedDao.removeOldArticles(feedId, threshold)
    }
}