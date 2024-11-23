package srimani7.apps.feedfly.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    labelRepository: LabelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val labelId: Long = savedStateHandle["id"] ?: -1

    val labelFlow = labelRepository.getLabel(labelId)
    val articlesFlow = labelRepository.getArticles(labelId)

}