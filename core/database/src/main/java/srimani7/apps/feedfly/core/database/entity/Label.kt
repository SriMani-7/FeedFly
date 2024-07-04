package srimani7.apps.feedfly.core.database.entity

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
    @ColumnInfo("pinned", defaultValue = "0")
    val priority: Boolean,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0,
)