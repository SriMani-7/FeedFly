package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.PrivateArticle

@Dao
interface PrivateSpaceDao {

    @Transaction
    @Query("""select a.article_id as articleId, a.title as title, a.description as description, a.link as articleLink, a.pub_date as publishedTime,
               am.mime_type as mediaType, am.url as mediaSrc, --- article media
               l.label_name as label, -- label
               f.feed_title as feedTitle, f.id as feedId -- feed
        from articles as a
        inner join feeds as f on f.id = a.feed_id
        left join articles_media as am on am.article_id = a.article_id
        left join article_labels as al on al.article_id = a.article_id
        left join labels as l on l.id = al.label_id
        where a.is_private = 1 and f.group_name = :group
    """)
    fun getPrivateArticles(group: String): Flow<List<PrivateArticle>>

    @Query("select distinct group_name from feeds as f inner join articles as a on a.feed_id = f.id where a.is_private = 1")
    fun getGroups(): Flow<List<String>>

    @Query("update articles set is_private = 0 where article_id = :it")
    suspend fun unLockArticle(it: Long)

    @Query("update articles set is_private = 1 where article_id = :l")
    suspend fun moveArticleToPrivate(l: Long)
}