package srimani7.apps.feedfly.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.feedfly.core.database.entity.ArticleTrash
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage

@Database(
    entities = [Feed::class,
        ArticleItem::class,
        FeedImage::class,
        ArticleMedia::class,
        ArticleTrash::class],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), // Unique index - title+link in articles
        AutoMigration(3, 4, AppDatabase.Migration4::class), // group names null to others
        AutoMigration(4, 5),
        AutoMigration(5, 6) // articles trash entity
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

    class Migration4 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            db.execSQL("drop index if exists index_articles_title_link")
            db.execSQL("update feeds set group_name = 'Others' where group_name is null")
        }
    }
}