package srimani7.apps.feedfly.core.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.model.FeedGroupModel
import srimani7.apps.feedfly.core.model.FeedModel
import srimani7.apps.feedfly.core.model.LabelData
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.core.model.SimpleFeed
import java.util.Date

@Dao
interface FeedDao {
    @Insert
    suspend fun insertFeedUrl(feed: Feed): Long

    @Update
    suspend fun updateFeedUrl(feed: Feed)

    @Query("select feed_url as feedUrl, description, link, feed_title as title, last_build_date as lastBuildDate, group_name as groupName, id from feeds where id = :id")
    fun getFeed(id: Long): Flow<FeedModel>

    @Query(
        """
        SELECT f.group_name AS name, COUNT(*) AS count
        FROM 
            feeds as f
        GROUP BY 
            f.group_name
    """
    )
    fun getFeedGroups(): Flow<List<FeedGroupModel>>

    @Query("select distinct group_name from feeds")
    fun getGroups(): Flow<List<String>>

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

    @Query("delete from feeds where id = :feed")
    suspend fun delete(feed: Long)

    @Transaction
    suspend fun removeOldArticles(feedId: Long, threshold: Long) {
        val current = Date().time
        moveToTrash(feedId, threshold, current)
        deleteArticles(feedId, threshold)
    }

    @Query(
        """
        DELETE from articles
        where pub_date <= :threshold 
        and feed_id = :feedId 
        and article_id not in (select article_id from article_labels)
        and is_private = 0
    """
    )
    fun deleteArticles(feedId: Long, threshold: Long)

    @Query(
        """insert into articles_trash (title, link, feed_id, last_delete) 
        select a.title, a.link, a.feed_id, :date 
       from articles as a
        left join article_labels as al on a.article_id = al.article_id
        where (a.pub_date <= :threshold and a.feed_id = :feedId and is_private = 0) and al.article_id is null"""
    )
    fun moveToTrash(feedId: Long, threshold: Long, date: Long)

    @Query(
        """
        select l.label_name as name, l.id as id, l.pinned as pinned, count(*) as count from labels as l 
            inner join article_labels as al on al.label_id = l.id
            inner join articles as a on a.article_id == al.article_id
            where a.feed_id = :feedId
            group by l.id
    """
    )
    fun getArticleLabels(feedId: Long): Flow<List<LabelData>>

    @Query(
        """
        select a.article_id as articleId, a.title as title, a.description AS description, a.link as articleLink, a.pub_date as publishedTime,
       am.mime_type as mediaType, am.url as mediaSrc,
       al.label_id as labelId from articles as a 
        left join articles_media as am ON a.article_id == am.article_id
            left join article_labels as al on a.article_id = al.article_id 
            where (a.is_private = 0 and a.feed_id = :id) and ((:unlabelled = 1 and al.label_id is null) or al.label_id = :labelId)
            order by a.pub_date desc
    """
    )
    fun getFeedArticles(id: Long, labelId: Long, unlabelled: Boolean): Flow<List<LabelledArticle>>

    @Query(
        """select f.id, f.feed_url as feedUrl, f.link, f.feed_title as title, f.last_build_date as lastBuildDate, fi.image_url as imageUrl from feeds f 
        left join feed_images fi on fi.feed_id = f.id
        where f.group_name = :groupName
    """
    )
    fun getFeeds(groupName: String): Flow<List<SimpleFeed>>

    @Query("select l.label_name as name, l.pinned as pinned, l.id as id, count(*) as count from labels l left join article_labels al on l.id = al.label_id where l.pinned = 1 group by l.id order by count desc")
    fun getPinnedLabels(): Flow<List<LabelData>>

    @Query("update feeds set group_name = :name where id = :id")
    suspend fun updateFeedGroup(id: Long, name: String)
}

fun dbErrorLog(message: String, throwable: Throwable? = null) {
    Log.e("room_ops", message, throwable)
}

fun dbInfoLog(vararg message: Any) {
    Log.i("room_ops", message.joinToString(" "))
}