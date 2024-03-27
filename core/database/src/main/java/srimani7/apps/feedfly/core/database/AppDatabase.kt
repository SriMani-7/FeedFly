package srimani7.apps.feedfly.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.dao.FeedDao
import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.ArticleLabel
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.feedfly.core.database.entity.ArticleTrash
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.database.entity.Label

@Database(
    entities = [Feed::class,
        ArticleItem::class,
        FeedImage::class,
        ArticleMedia::class,
        ArticleTrash::class,
        Label::class,
        ArticleLabel::class
    ],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), // Unique index - title+link in articles
        AutoMigration(3, 4, AppDatabase.Migration4::class), // group names null to others
        AutoMigration(4, 5),
        AutoMigration(5, 6), // articles trash entity
        AutoMigration(6, 7) // Label & ArticleLabel entities
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun articleDao(): ArticleDao

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