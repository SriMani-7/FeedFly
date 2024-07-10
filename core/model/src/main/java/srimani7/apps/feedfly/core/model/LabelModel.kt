package srimani7.apps.feedfly.core.model

data class LabelModel(
    val labelName: String,
    val pinned: Boolean,
    val id: Long = 0,
)
