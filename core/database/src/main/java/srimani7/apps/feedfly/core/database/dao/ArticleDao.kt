package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.database.entity.Label
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
        SELECT l.id AS id, l.label_name As name, COUNT(al.article_id) AS count, l.pinned as pinned 
FROM labels l
LEFT JOIN article_labels al ON l.id = al.label_id
GROUP BY l.id
ORDER BY l.id
    """)
    fun getLabels(): Flow<List<LabelData>>

    @Query("REPLACE INTO article_labels (article_id, label_id) VALUES(:articleId, :labelId);")
    suspend fun updateLabel(articleId: Long, labelId: Long)

    @Query("delete from article_labels where article_id = :articleId")
    suspend fun removeArticleLabel(articleId: Long)

    @Insert
    suspend fun addLabel(label: Label)
}