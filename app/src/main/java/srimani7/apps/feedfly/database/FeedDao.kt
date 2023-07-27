package srimani7.apps.feedfly.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage
import srimani7.apps.rssparser.debugLog

@Dao
interface FeedDao {
    @Insert
    suspend fun insertFeedUrl(feed: Feed): Long

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
    @Query("select * from feeds order by last_build_date desc")
    fun getAllFeeds(): Flow<List<FeedDto>>

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(articleMedia: ArticleMedia)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(feedImage: FeedImage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feedImage: ArticleItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feedImage: FeedImage): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(articleMedia: ArticleMedia): Long

    @Throws(UnsupportedOperationException::class)
    @Transaction
    suspend fun <T> insertOrUpdate(entity: T) {
        val rowId = when(entity) {
            is Feed -> insertFeedUrl(entity)
            is ArticleItem -> insert(entity)
            is ArticleMedia -> insert(entity)
            is FeedImage -> insert(entity)
            else -> throw UnsupportedOperationException("Wrong entity $entity")
        }
        debugLog("$rowId $entity")
        if (rowId == -1L) {
            when(entity) {
                is Feed -> updateFeedUrl(entity)
                is ArticleItem -> updateArticle(entity)
                is ArticleMedia -> update(entity)
                is FeedImage -> update(entity)
                else -> throw UnsupportedOperationException("Wrong entity $entity")
            }
        }
    }

    @Transaction
    @Query("select title, link, category, pinned, pub_date, description, author, article_id from articles where feed_id = :feedId ORDER BY articles.pub_date desc")
    fun getArticles(feedId: Long): Flow<List<FeedArticle>>

    @Transaction
    @Query("select title, link, category, pinned, pub_date, description, author, article_id from articles ORDER BY articles.pub_date desc")
    fun getArticles(): Flow<List<FeedArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleItem: ArticleItem, articleMedia: ArticleMedia)
    @Delete
    suspend fun delete(feed: Feed)

    @Query("select article_id from articles where link = :rowId")
    fun getArticle(rowId: String): Flow<Long>

    @Query("select * from feeds")
    fun getFeedUrls(): Flow<List<Feed>>

}