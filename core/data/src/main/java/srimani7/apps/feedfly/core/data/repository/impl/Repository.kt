package srimani7.apps.feedfly.core.data.repository.impl

import srimani7.apps.feedfly.core.data.asFeed
import srimani7.apps.feedfly.core.database.dao.FeedDao
import srimani7.apps.rssparser.elements.Channel
import javax.inject.Inject

class Repository @Inject constructor(
    private val feedDao: FeedDao
) {

    fun getGroups() = feedDao.getGroups()
    fun getAllFeeds() = feedDao.getAllFeeds()

    suspend fun insertFeedUrl(channel: Channel, groupName: String) {
        feedDao.insertFeedUrl(channel.asFeed(groupName))
    }

    suspend fun removeOldArticles(feedId: Long, threshold: Long) {
        feedDao.removeOldArticles(feedId, threshold)
    }

}

