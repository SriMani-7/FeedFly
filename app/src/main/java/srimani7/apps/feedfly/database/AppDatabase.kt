package srimani7.apps.feedfly.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage

@Database(
    entities = [Feed::class, ArticleItem::class, FeedImage::class, ArticleMedia::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (!Companion::instance.isInitialized) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "daily-database"
                    ).build()
                }
            }
            return instance
        }
    }
}