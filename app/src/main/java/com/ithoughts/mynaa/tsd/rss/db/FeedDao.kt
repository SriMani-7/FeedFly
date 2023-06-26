package com.ithoughts.mynaa.tsd.rss.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
    @Query("select * from feeds where id = :id")
    fun getFeedArticles(id: Long): Flow<FeedArticle>

    @Query("select * from feeds where id = :id")
    fun getFeed(id: Long): Flow<Feed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedWithArticles(feed: Feed, articles: List<ArticleItem>)

}