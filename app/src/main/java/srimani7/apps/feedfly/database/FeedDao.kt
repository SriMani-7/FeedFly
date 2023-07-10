package srimani7.apps.feedfly.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed

@Dao
interface FeedDao {
    @Insert
    suspend fun insertFeedUrl(feed: Feed)

    @Update
    suspend fun updateFeedUrl(feed: Feed)

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

    @Update
    suspend fun updateArticle(articleItem: ArticleItem)

    @Query("select * from feeds where group_name is null")
    fun getOtherFeeds(): Flow<List<Feed>?>

    @Transaction
    @MapInfo(keyColumn = "name")
    @Query("SELECT feeds.group_name AS name, articles.*, feed_title FROM feeds INNER JOIN articles ON feeds.id = articles.feed_id WHERE articles.pinned = :isPinned")
    fun getFavoriteFeedArticles(isPinned: Boolean = true): Flow<Map<String?, List<FavoriteArticle>>>

    @Query("update articles set pinned = :pinned where article_id = :id")
    suspend fun updateArticlePin(id: Long, pinned: Boolean)

    @Query("select distinct group_name from feeds")
    fun getGroups(): Flow<List<String?>>
}