package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.LabelData
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

    @Query("""
        SELECT l.id AS id, l.label_name As name, COUNT(al.article_id) AS count, l.priority as priority 
FROM labels l
LEFT JOIN article_labels al ON l.id = al.label_id
GROUP BY l.id
ORDER BY l.id
    """)
    fun getLabels(): Flow<List<LabelData>>

    @Query("insert into article_labels (article_id, label_id) values(:l, (select id from labels where label_name = 'private'))")
    suspend fun moveArticleToPrivate(l: Long)
}