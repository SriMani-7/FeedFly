package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
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

}