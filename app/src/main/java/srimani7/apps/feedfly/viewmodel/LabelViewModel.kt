package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import srimani7.apps.feedfly.core.database.LabelRepository
import srimani7.apps.feedfly.data.UserSettingsRepo

class LabelViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val labelRepository = LabelRepository(application)
    private val labelId: Long = savedStateHandle["id"] ?: -1
    private val userSettingsRepo = UserSettingsRepo(application)

    val labelFlow = labelRepository.getLabel(labelId)
    val articlesFlow = labelRepository.getArticles(labelId)

    val articlePreferencesFlow by lazy { userSettingsRepo.articlePreferences }
}