package srimani7.apps.feedfly.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import srimani7.apps.feedfly.core.database.dao.ArticleDao
import srimani7.apps.feedfly.core.database.dao.FeedDao
import srimani7.apps.feedfly.core.database.dao.PrivateSpaceDao
import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.ArticleLabel
import srimani7.apps.feedfly.core.database.entity.ArticleMedia
import srimani7.apps.feedfly.core.database.entity.ArticleTrash
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.database.entity.Label
import srimani7.apps.feedfly.core.database.migrations.Migration4
import srimani7.apps.feedfly.core.database.migrations.Migration9to10

@Database(
    entities = [Feed::class,
        ArticleItem::class,
        FeedImage::class,
        ArticleMedia::class,
        ArticleTrash::class,
        Label::class,
        ArticleLabel::class
    ],
    version = 10,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), // Unique index - title+link in articles
        AutoMigration(3, 4, Migration4::class), // group names null to others
        AutoMigration(4, 5),
        AutoMigration(5, 6), // articles trash entity
        AutoMigration(6, 7), // Label & ArticleLabel entities
        // Manual migration from 7 to 8
        AutoMigration(9, 10, Migration9to10::class), // isPrivate in article and removed priority in label
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun articleDao(): ArticleDao
    abstract fun privateSpaceDao(): PrivateSpaceDao

    companion object {

        /**
         * moving pinned articles under the favorite label.
         * **/
        internal val MIGRATION_7_8 = Migration(7, 8) { database ->
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

        internal val MIGRATION_8_9 = Migration(8, 9) { database ->
            // delete rows from the article_labels table where the article_id is repeated more than once,
            database.execSQL("DELETE FROM article_labels\n" +
                    "WHERE id NOT IN (\n" +
                    "    SELECT MIN(id)\n" +
                    "    FROM article_labels\n" +
                    "    GROUP BY article_id\n" +
                    ");")

            // Drop the index related to the article_column
            database.execSQL("drop index if exists index_article_labels_article_id")

            // Create unique index on article_labels
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `article_labels_article_id_unique_index` ON `article_labels` (`article_id`)")
        }
    }

}