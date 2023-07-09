package srimani7.apps.feedfly.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserSettingsRepo(private val application: Application) {
    private val themePreferenceKey = stringPreferencesKey("app_theme_preference")

    suspend fun updateSettings(newTheme: AppTheme) {
        application.dataStore.edit { settings ->
            settings[themePreferenceKey] = newTheme.name
        }
    }

    fun appThemeFlow(coroutineScope: CoroutineScope) = application.dataStore.data
        .map { AppTheme.valueOf(it[themePreferenceKey] ?: AppTheme.SYSTEM_DEFAULT.name) }
        .stateIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM_DEFAULT
        )
}