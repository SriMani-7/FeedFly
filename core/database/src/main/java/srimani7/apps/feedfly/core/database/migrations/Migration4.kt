package srimani7.apps.feedfly.core.database.migrations

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration4 : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)
        db.execSQL("drop index if exists index_articles_title_link")
        db.execSQL("update feeds set group_name = 'Others' where group_name is null")
    }
}