package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.preferences.AppTheme
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    val settingsFlow = userSettingsRepo.settingsFlow
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