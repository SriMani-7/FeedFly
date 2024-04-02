package srimani7.apps.feedfly.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserSettingsRepo(private val application: Application) {
    private val themePreferenceKey = stringPreferencesKey("app_theme_preference")
    private val currentGroupKey = stringPreferencesKey("current_group_preference")
    private val useDynamicTheme = booleanPreferencesKey("use_dynamic_theme_preference")

    val settingsFlow = application.dataStore.data.map {
        Settings(
            theme = AppTheme.valueOf(it[themePreferenceKey] ?: AppTheme.SYSTEM_DEFAULT.name),
            currentGroup = it[currentGroupKey] ?: "",
            useDynamicTheme = it[useDynamicTheme] ?: false
        )
    }

    suspend fun updateSettings(newTheme: AppTheme) {
        application.dataStore.edit { settings ->
            settings[themePreferenceKey] = newTheme.name
        }
    }

    suspend fun useDynamicTheming(value: Boolean) {
        application.dataStore.edit { settings ->
            settings[useDynamicTheme] = value
        }
    }

    suspend fun setCurrentGroup(group: String) {
        application.dataStore.edit {
            it[currentGroupKey] = group
        }
    }

    data class Settings(
        val theme: AppTheme,
        val currentGroup: String,
        val useDynamicTheme: Boolean = false
    )
}