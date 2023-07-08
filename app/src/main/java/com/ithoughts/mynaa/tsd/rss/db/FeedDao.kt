package com.ithoughts.mynaa.tsd.rss.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ithoughts.mynaa.tsd.rss.ui.FeedGroup
import kotlinx.coroutines.flow.Flow


// queries lies on D:\Ithoughts\Mynaa\the_secret_dairy\queries.sql

@Dao
interface FeedDao {
    @Insert
    suspend fun insertFeedUrl(feed: Feed)

    @Delete
    suspend fun deleteFeedUrl(feed: Feed)

    @Update
    suspend fun updateFeedUrl(feed: Feed)

    @Query("select * from feeds")
    fun getAllFeedUrls(): Flow<List<Feed>>

    @Transaction
    @Query("select * from feeds INNER JOIN articles ON feeds.id = articles.feed_id WHERE feeds.id = :id ORDER BY articles.pub_date desc")
    fun getFeedArticles(id: Long): Flow<List<ArticleItem>>

    @Query("select * from feeds where id = :id")
    fun getFeed(id: Long): Flow<Feed>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeedArticles(articles: List<ArticleItem>)

    @Transaction
    @Query("select group_name as name, * from feeds where group_name not null group by group_name")
    fun getAllGroups(): Flow<List<FeedGroup>>

    @Query("select * from feeds where group_name is :name order by last_build_date desc")
    fun getAllFeedUrls(name: String?): Flow<List<Feed>>

    @Update
    suspend fun updateArticle(articleItem: ArticleItem)

    @Query("select * from feeds where group_name is null")
    fun getOtherFeeds(): Flow<List<Feed>?>
}