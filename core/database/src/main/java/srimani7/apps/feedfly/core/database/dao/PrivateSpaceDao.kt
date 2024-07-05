package srimani7.apps.feedfly.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivateSpaceDao {

    @Query("select distinct group_name from feeds as f inner join articles as a on a.feed_id = f.id where a.is_private = 1")
    fun getGroups(): Flow<List<String>>

}