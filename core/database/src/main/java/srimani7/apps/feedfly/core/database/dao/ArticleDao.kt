package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.database.entity.Label
import srimani7.apps.feedfly.core.model.LabelData
import srimani7.apps.feedfly.core.model.LabelModel
import srimani7.apps.feedfly.core.model.LabelledArticle
import java.util.Date

@Dao
interface ArticleDao {

    @Transaction
    suspend fun deleteArticle(articleId: Long) {
        val current = Date().time
        moveToTrash(articleId, current)
        deleteArticleInt(articleId)
    }

    @Query("delete from articles where article_id = :articleId")
    fun deleteArticleInt(articleId: Long)

    @Query("insert into articles_trash (title, link, feed_id, last_delete) select title, link, feed_id, :date from articles where article_id = :articleId")
    fun moveToTrash(articleId: Long, date: Long)

    @Query(
        """
        SELECT l.id AS id, l.label_name As name, COUNT(al.article_id) AS count, l.pinned as pinned 
FROM labels l
LEFT JOIN article_labels al ON l.id = al.label_id
GROUP BY l.id
ORDER BY l.id
    """
    )
    fun getLabels(): Flow<List<LabelData>>

    @Query("update articles set is_private = 1 where article_id = :l")
    suspend fun moveArticleToPrivate(l: Long)

    @Query("REPLACE INTO article_labels (article_id, label_id) VALUES(:articleId, :labelId);")
    suspend fun updateLabel(articleId: Long, labelId: Long)

    @Query("delete from article_labels where article_id = :articleId")
    suspend fun removeArticleLabel(articleId: Long)

    @Insert
    suspend fun addLabel(label: Label)

    @Query("select label_name as labelName, pinned, id from labels where id = :id")
    fun getLabel(id: Long): Flow<LabelModel?>

    @Query(
        """
        select a.article_id as articleId, a.title as title, a.description AS description, a.link as articleLink, a.pub_date as publishedTime,
       am.mime_type as mediaType, am.url as mediaSrc,
       al.label_id as labelId from articles as a 
        left join articles_media as am ON a.article_id == am.article_id
            left join article_labels as al on a.article_id = al.article_id 
            where a.is_private = 0 and al.label_id = :label
            order by a.pub_date desc
    """
    )
    fun getArticles(label: Long): Flow<List<LabelledArticle>>
}