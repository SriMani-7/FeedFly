package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import srimani7.apps.feedfly.core.database.LabelRepository

class LabelViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val labelRepository = LabelRepository(application)
    private val labelId: Long = savedStateHandle["id"] ?: -1

    val labelFlow = labelRepository.getLabel(labelId)
}