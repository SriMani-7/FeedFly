package srimani7.apps.feedfly.database

import android.util.Log
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
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage

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
    @Query("SELECT feeds.group_name AS name, title, articles.link, articles.category, articles.pub_date, articles.pinned,articles.description, articles.author, articles.article_id FROM feeds INNER JOIN articles as articles ON feeds.id = articles.feed_id WHERE articles.pinned = :isPinned")
    fun getFavoriteFeedArticles(isPinned: Boolean = true): Flow<Map<String?, List<FeedArticle>>>

    @Query("update articles set pinned = :pinned where article_id = :id")
    suspend fun updateArticlePin(id: Long, pinned: Boolean)

    @Query("select distinct group_name from feeds")
    fun getGroups(): Flow<List<String?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(feedImage: FeedImage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feedImage: ArticleItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feedImage: FeedImage): Long

    @Query(
        "insert into articles_media " +
                "(media_size, mime_type, url, article_id) " +
                "values(:mediaSize, :mediaType, :url, " +
                "(select article_id from articles where link = :articleLink and title = :articleTitle))"
    )
    suspend fun insertArticleMedia(
        mediaSize: Long?,
        mediaType: String?,
        url: String?,
        articleLink: String,
        articleTitle: String
    )

    @Transaction
    @Query("select title, link, category, pinned, pub_date, description, author, article_id from articles where feed_id = :feedId ORDER BY articles.pub_date desc")
    fun getArticles(feedId: Long): Flow<List<FeedArticle>>

    @Transaction
    @Query("select title, link, category, pinned, pub_date, description, author, article_id from articles ORDER BY articles.pub_date desc")
    fun getArticles(): Flow<List<FeedArticle>>

    @Delete
    suspend fun delete(feed: Feed)

    @Query("select article_id from articles where link = :rowId")
    fun getArticle(rowId: String): Flow<Long>

    @Query("select * from feeds")
    fun getFeedUrls(): Flow<List<Feed>>

    @Transaction
    @Query("select title, link, category, pinned, pub_date, description, author, article_id from articles where article_id = :id")
    fun getFeedArticle(id: Long): Flow<FeedArticle?>

}

fun dbErrorLog(message: String, throwable: Throwable? = null) {
    Log.e("room_ops", message, throwable)
}

fun dbInfoLog(vararg messge: Any) {
    Log.i("room_ops", messge.joinToString(" "))
}