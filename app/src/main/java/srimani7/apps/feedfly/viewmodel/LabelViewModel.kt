package srimani7.apps.feedfly.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    labelRepository: LabelRepository,
    private val userSettingsRepo: UserSettingsRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val labelId: Long = savedStateHandle["id"] ?: -1

    val labelFlow = labelRepository.getLabel(labelId)
    val articlesFlow = labelRepository.getArticles(labelId)

    val articlePreferencesFlow by lazy { userSettingsRepo.articlePreferences }
}