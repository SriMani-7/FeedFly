package srimani7.apps.feedfly.core.database.entity

import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "labels",
    indices = [
        Index("label_name", unique = true, name = "article_label_name_index")
    ]
)
data class Label(
    @ColumnInfo("label_name")
    val labelName: String,
    @ColumnInfo("priority")
    @ArticleLabelPriority
    val priority: Short,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0,
)

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    ArticleLabelPriority.HIGH,
    ArticleLabelPriority.MEDIUM,
    ArticleLabelPriority.NORMAL,
    ArticleLabelPriority.LOW
)
annotation class ArticleLabelPriority {
    companion object {
        const val HIGH = 4
        const val MEDIUM = 3
        const val NORMAL = 2
        const val LOW = 1
    }
}