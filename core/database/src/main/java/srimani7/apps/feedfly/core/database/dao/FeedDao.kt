package srimani7.apps.feedfly.core.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.database.dto.FeedArticle
import srimani7.apps.feedfly.core.database.dto.FeedDto
import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.model.LabelData
import srimani7.apps.feedfly.core.model.LabelledArticle
import java.util.Date

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

    @Query("select distinct group_name from feeds")
    fun getGroups(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(feedImage: FeedImage)

    @Query(
        """INSERT INTO articles (title, link, category, feed_id, lastFetch, pub_date, description, author)
    SELECT :title,:link,:category,:feedId,:lastFetch,:pubDate,:description,:author
    WHERE NOT EXISTS (
        SELECT 1
        FROM articles_trash
        WHERE title = :title AND link = :link and feed_id = :feedId
    ) and not exists (
        SELECT 1
        FROM articles
        WHERE title = :title AND link = :link and feed_id = :feedId
    )"""
    )
    suspend fun insertArticle(
        title: String,
        link: String,
        category: String,
        feedId: Long,
        lastFetch: Date?,
        pubDate: Date?,
        description: String?,
        author: String?,
    ): Long

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
    @Query("select title, link, category, pub_date, description, author, article_id from articles where feed_id = :feedId ORDER BY articles.pub_date desc")
    fun getArticles(feedId: Long): Flow<List<FeedArticle>>

    @Transaction
    @Query("select title, link, category, pub_date, description, author, article_id from articles ORDER BY articles.pub_date desc")
    fun getArticles(): Flow<List<FeedArticle>>

    @Delete
    suspend fun delete(feed: Feed)

    @Query("select article_id from articles where link = :rowId")
    fun getArticle(rowId: String): Flow<Long>

    @Query("select * from feeds")
    fun getFeedUrls(): Flow<List<Feed>>

    @Transaction
    @Query("select title, link, category, pub_date, description, author, article_id from articles where article_id = :id")
    fun getFeedArticle(id: Long): Flow<FeedArticle?>

    @Transaction
    suspend fun removeOldArticles(feedId: Long, threshold: Long) {
        val current = Date().time
        moveToTrash(feedId, threshold, current)
        deleteArticles(feedId, threshold)
    }

    @Query("""
        DELETE from articles
        where pub_date <= :threshold 
        and feed_id = :feedId 
        and article_id not in (select article_id from article_labels)
    """)
    fun deleteArticles(feedId: Long, threshold: Long)

    @Query("""insert into articles_trash (title, link, feed_id, last_delete) 
        select a.title, a.link, a.feed_id, :date 
       from articles as a
        left join article_labels as al on a.article_id = al.article_id
        where (a.pub_date <= :threshold and a.feed_id = :feedId) and al.article_id is null""")
    fun moveToTrash(feedId: Long, threshold: Long, date: Long)

    @Query("""
       SELECT a.article_id as articleId, a.title as title, a.description AS description, a.link as articleLink, a.pub_date as publishedTime,
       am.mime_type as mediaType, am.url as mediaSrc,
       l.label_name as label, al.label_id as labelId
        from articles as a
        left join articles_media as am ON a.article_id == am.article_id
        left join article_labels as al ON a.article_id == al.article_id
        left join labels as l ON al.label_id == l.id
        where a.feed_id = :feedId and (l.label_name != 'private' or l.label_name is null)
        order by a.pub_date desc
    """)
    fun getLabelledArticles(feedId: Long): Flow<List<LabelledArticle>>

    @Query("""
        select l.label_name as name, l.id as id, l.priority as priority, count(*) as count from labels as l 
            inner join article_labels as al on al.label_id = l.id
            inner join articles as a on a.article_id == al.article_id
            where a.feed_id = :feedId
            group by l.id
    """)
    fun getArticleLabels(feedId: Long): Flow<List<LabelData>>
}

fun dbErrorLog(message: String, throwable: Throwable? = null) {
    Log.e("room_ops", message, throwable)
}

fun dbInfoLog(vararg messge: Any) {
    Log.i("room_ops", messge.joinToString(" "))
}