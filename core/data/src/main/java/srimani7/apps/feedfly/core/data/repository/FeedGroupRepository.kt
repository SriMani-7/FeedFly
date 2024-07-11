package srimani7.apps.feedfly.core.data.repository

import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.SimpleFeed

interface FeedGroupRepository {
    fun getFeeds(name: String): Flow<List<SimpleFeed>>
    fun getGroups(): Flow<List<String>>
}