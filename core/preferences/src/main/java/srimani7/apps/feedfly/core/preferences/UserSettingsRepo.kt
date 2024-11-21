package srimani7.apps.feedfly.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import srimani7.apps.feedfly.core.preferences.model.ArticlePreference
import srimani7.apps.feedfly.core.preferences.model.ReadLaterRemainder
import srimani7.apps.feedfly.core.preferences.model.ThemePreference
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserSettingsRepo @Inject constructor(@ApplicationContext private val application: Context) {
    private val themePreferenceKey = stringPreferencesKey("app_theme_preference")
    private val useDynamicTheme = booleanPreferencesKey("use_dynamic_theme_preference")
    private val aSwipeDeleteKey = booleanPreferencesKey("article_swipe_delete_preference")
    private val aLongClickPrivateKey = booleanPreferencesKey("article_long_click_preference")
    private val remainderHourKey = intPreferencesKey("read_later_hour_preference")
    private val remainderMinuteKey = intPreferencesKey("read_later_minute_preference")

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

    val remainderTimeFlow = application.dataStore.data.map {
        ReadLaterRemainder(
            hour = it[remainderHourKey] ?: return@map null,
            minute = it[remainderMinuteKey] ?: return@map null
        )
    }

    suspend fun updateRemainderTime(readLaterRemainder: ReadLaterRemainder?) {
        application.dataStore.edit {
            if (readLaterRemainder == null) {
                it.remove(remainderHourKey)
                it.remove(remainderMinuteKey)
            } else {
                it[remainderHourKey] = readLaterRemainder.hour
                it[remainderMinuteKey] = readLaterRemainder.minute
            }
        }
    }

}