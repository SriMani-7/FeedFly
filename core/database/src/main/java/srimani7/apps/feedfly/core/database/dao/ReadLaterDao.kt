package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import srimani7.apps.feedfly.core.model.LabelledArticle

@Dao
interface ReadLaterDao {
    @Query("insert into read_later_articles (article_id) values (:articleId)")
    suspend fun markArticle(articleId: Long)

    @Query("delete from read_later_articles where article_id = :articleId")
    suspend fun unMarkArticle(articleId: Long)

    @Query("select count(*) from read_later_articles")
    fun countArticles(): Flow<Long>

    @Query(
        """select a.article_id as articleId, a.title as title, a.description AS description, a.link as articleLink, a.pub_date as publishedTime
            from read_later_articles as rla left join articles as a ON a.article_id == rla.article_id
        """
    )
    fun getArticles(): Flow<List<LabelledArticle>>

}