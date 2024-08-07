package srimani7.apps.feedfly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepo: UserSettingsRepo
) : ViewModel() {

    val themePreferenceFlow = userSettingsRepo.themePreferenceFlow
    val articlePreferencesFlow = userSettingsRepo.articlePreferences

    fun useDynamicTheme(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { userSettingsRepo.useDynamicTheming(value) }
    }

    fun updateSettings(appTheme: AppTheme) {
        viewModelScope.launch {
            userSettingsRepo.updateSettings(appTheme)
        }
    }

    fun setArticleSwipe(delete: Boolean) {
        viewModelScope.launch { userSettingsRepo.updateArticleSwipe(delete) }
    }

    fun setArticleLongClick(private: Boolean) {
        viewModelScope.launch { userSettingsRepo.updateArticleLongClick(private) }
    }
}