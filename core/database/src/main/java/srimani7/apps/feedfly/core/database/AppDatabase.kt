package srimani7.apps.feedfly.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
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
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), // Unique index - title+link in articles
        AutoMigration(3, 4, AppDatabase.Migration4::class), // group names null to others
        AutoMigration(4, 5),
        AutoMigration(5, 6), // articles trash entity
        AutoMigration(6, 7), // Label & ArticleLabel entities
        // Manual migration from 7 to 8
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
                    ).addMigrations(MIGRATION_7_8)
                        .build()
                }
            }
            return instance
        }

        /**
         * moving pinned articles under the favorite label.
         * **/
        private val MIGRATION_7_8 = Migration(7, 8) { database ->
            // insert favorite & private labels into labels table.
            database.execSQL("insert or replace into labels (label_name, priority, id) values('favorite', 3, 1)")
            database.execSQL("insert or replace into labels (label_name, priority, id) values('private', 0, 2)")

            // move the pinned articles into the favorite label
            database.execSQL("insert into article_labels (article_id, label_id) select article_id, 1 from articles where pinned = 1")

            // drop column pinned from articles
            database.execSQL("CREATE TABLE IF NOT EXISTS `_new_articles` (`title` TEXT NOT NULL, `link` TEXT NOT NULL, `category` TEXT NOT NULL, `feed_id` INTEGER NOT NULL, `lastFetch` INTEGER, `pub_date` INTEGER, `description` TEXT, `author` TEXT, `article_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`feed_id`) REFERENCES `feeds`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
            database.execSQL("INSERT INTO `_new_articles` (`title`,`link`,`category`,`feed_id`,`lastFetch`,`pub_date`,`description`,`author`,`article_id`) SELECT `title`,`link`,`category`,`feed_id`,`lastFetch`,`pub_date`,`description`,`author`,`article_id` FROM `articles`")
            database.execSQL("DROP TABLE `articles`")
            database.execSQL("ALTER TABLE `_new_articles` RENAME TO `articles`")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_articles_feed_id_title_link` ON `articles` (`feed_id`, `title`, `link`)")
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