package srimani7.apps.feedfly.core.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import srimani7.apps.feedfly.core.preferences.model.ThemePreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserSettingsRepo(private val application: Application) {
    private val themePreferenceKey = stringPreferencesKey("app_theme_preference")
    private val currentGroupKey = stringPreferencesKey("current_group_preference")
    private val useDynamicTheme = booleanPreferencesKey("use_dynamic_theme_preference")
    private val aSwipeDeleteKey = booleanPreferencesKey("article_swipe_delete_preference")
    private val aLongClickPrivateKey = booleanPreferencesKey("article_long_click_preference")

    val currentGroupFlow = application.dataStore.data.map { it[currentGroupKey] }

    val themePreferenceFlow = application.dataStore.data.map {
        ThemePreference(
            theme = AppTheme.valueOf(it[themePreferenceKey] ?: AppTheme.SYSTEM_DEFAULT.name),
            useDynamicTheme = it[useDynamicTheme] ?: false
        )
    }

    val articlePreferences = application.dataStore.data.map {
        ArticlePreference(
            swipeToDelete = it[aSwipeDeleteKey] ?: false,
            longClickToPrivate = it[aLongClickPrivateKey] ?: false
        )
    }


    private suspend fun <K> editPreference(key: Preferences.Key<K>, value: K) {
        application.dataStore.edit { it[key] = value }
    }

    suspend fun updateArticleSwipe(delete: Boolean) = editPreference(aSwipeDeleteKey, delete)
    suspend fun updateArticleLongClick(private: Boolean) =
        editPreference(aLongClickPrivateKey, private)

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

    data class ArticlePreference(
        val swipeToDelete: Boolean = false,
        val longClickToPrivate: Boolean = false
    )
}