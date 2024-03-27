package srimani7.apps.feedfly.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "article_labels",
    foreignKeys = [
        ForeignKey(
            entity = ArticleItem::class,
            parentColumns = ["article_id"],
            childColumns = ["article_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ), ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["label_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)
data class ArticleLabel(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("article_id", index = true) val articleId: Long,
    @ColumnInfo("label_id", index = true) val labelId: Long,
)
