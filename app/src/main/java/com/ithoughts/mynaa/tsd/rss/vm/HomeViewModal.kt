package com.ithoughts.mynaa.tsd.rss.vm

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ithoughts.mynaa.tsd.rss.OkHttpWebService
import com.ithoughts.mynaa.tsd.rss.RssParser
import com.ithoughts.mynaa.tsd.rss.dataStore
import com.ithoughts.mynaa.tsd.rss.db.AppDatabase
import com.ithoughts.mynaa.tsd.rss.ui.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    private val okHttpWebService by lazy { OkHttpWebService() }
    val groupsFlow by lazy { feedDao.getAllGroups() }
    val otherFeeds by lazy { feedDao.getOtherFeeds() }

    private val rssParser by lazy { RssParser() }
    var isLoading by mutableStateOf(false)
    val appThemeState = userSettingsRepo.appThemeFlow(viewModelScope)

    fun insertFeed(it: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val inputStream = okHttpWebService.getXMlString(it)
                val feed = inputStream?.let { it1 -> rssParser.parseFeed(it, it1) }
                isLoading = if (feed != null) {
                    feedDao.insertFeedUrl(feed)
                    false
                } else {
                    Toast.makeText(getApplication(), "Unable to parse url", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } catch (e: Exception) {
                isLoading = false
                e.printStackTrace()
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateSettings(newTheme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) { userSettingsRepo.updateSettings(newTheme) }
    }
}

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