package srimani7.apps.feedfly.core.database

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import srimani7.apps.feedfly.core.database.dao.dbErrorLog
import srimani7.apps.feedfly.core.database.dao.dbInfoLog
import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.database.entity.Label
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.Channel
import srimani7.apps.rssparser.elements.ChannelImage
import srimani7.apps.rssparser.elements.ChannelItem
import java.util.Date

class Repository(application: Application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val articleDao by lazy { AppDatabase.getInstance(application).articleDao() }

    fun getFeed(feedId: Long) = feedDao.getFeed(feedId)
    fun getGroups() = feedDao.getGroups()
    fun getFeedGroups() = feedDao.getFeedGroups()
    fun getAllFeeds() = feedDao.getAllFeeds()
    fun getFeeds(groupName: String) = feedDao.getFeeds(groupName)
    fun getPinnedLabels() = feedDao.getPinnedLabels()

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

    suspend fun insertFeedUrl(channel: Channel, groupName: String) {
        feedDao.insertFeedUrl(channel.asFeed(groupName))
    }

    suspend fun removeOldArticles(feedId: Long, threshold: Long) {
        feedDao.removeOldArticles(feedId, threshold)
    }

    suspend fun updateAndInsertArticles(feed: Feed, channel: Channel) = withContext(Dispatchers.IO) {
        updateFeedUrl(feed.copy(channel))
        channel.image?.let {
            launch {
                insert(it.asFeedImage(feed.id))
                cancel()
            }
        }
        channel.items.forEach { channelItem ->
            val article = channelItem.asArticleItem(feed.id)
            val rowId = insertArticle(
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
                    insertArticleMedia(
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

    private fun ChannelItem.asArticleItem(feedId: Long) = ArticleItem(
        title = title ?: "",
        link = link ?: "",
        category = categories.joinToString(separator = ", "),
        lastFetched = Date(),
        pubDate = DateParser.parseDate(pubDate),
        description = description,
        author = author,
        feedId = feedId
    )

    private fun ChannelImage.asFeedImage(feedId: Long) = FeedImage(
        link = link ?: "",
        title = title ?: "",
        url = url ?: "",
        feedId = feedId,
        description = description,
        height = height ?: FeedImage.DEFAULT_HEIGHT,
        width = width ?: FeedImage.DEFAULT_WIDTH
    )

    private fun Feed.copy(channel: Channel) = copy(
        description = channel.description,
        link = channel.link ?: "",
        title = channel.title ?: "",
        lastBuildDate = channel.lastBuildDate ?: Date(),
        language = channel.language,
        managingEditor = channel.managingEditor,
        copyright = channel.copyright
    )

    private fun Channel.asFeed(group: String) = Feed(
        feedUrl = feedUrl,
        description = description,
        link = link ?: "",
        title = title ?: "",
        lastBuildDate = null,
        group = group,
        language = language,
        managingEditor = managingEditor,
        copyright = copyright
    )

    suspend fun deleteArticle(articleId: Long) {
        articleDao.deleteArticle(articleId)
    }

    suspend fun moveArticleToPrivate(l: Long) {
        articleDao.moveArticleToPrivate(l)
    }

    fun getArticleLabels(feedId: Long) = feedDao.getArticleLabels(feedId)
    fun getFeedArticles(feedId: Long, id: Long?) = feedDao.getFeedArticles(feedId, id ?: -1, id == null)
}

class LabelRepository(application: Application) {
    private val articleDao by lazy { AppDatabase.getInstance(application).articleDao() }

    fun getAllLabels() = articleDao.getLabels()

    suspend fun updateArticleLabel(articleId: Long, labelId: Long) = articleDao.updateLabel(articleId, labelId)

    suspend fun removeArticleLabel(articleId: Long) = articleDao.removeArticleLabel(articleId)

    suspend fun addLabel(it: String) {
        articleDao.addLabel(Label(it, false))
    }

    fun getLabel(id: Long) = articleDao.getLabel(id)
}