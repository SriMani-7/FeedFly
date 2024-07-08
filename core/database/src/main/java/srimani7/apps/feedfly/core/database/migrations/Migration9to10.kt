package srimani7.apps.feedfly.core.database.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * AutoMigration Specification for the migrating private labelled articles into new scheme.
 * */
@DeleteColumn("labels", "priority")
class Migration9to10: AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)
        db.execSQL("""
            update articles set is_private = 1 where article_id in (
            select al.article_id from article_labels as al
            inner join labels as l on al.label_id = l.id
            where l.label_name = 'private');
        """.trimIndent())

        db.execSQL("delete from labels where label_name = 'private'")
    }
}