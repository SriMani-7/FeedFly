package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.Repository
import srimani7.apps.feedfly.core.preferences.AppTheme
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo
import srimani7.apps.rssparser.RssParserRepository
import srimani7.apps.rssparser.elements.Channel
import java.time.Instant
import java.time.temporal.ChronoUnit

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    val groupNameFlow by lazy {
        repository.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    val feedGroupsFlow = repository.getFeedGroups()
    val pinnedLabelsFlow = repository.getPinnedLabels()

    val settingsStateFlow = userSettingsRepo.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UserSettingsRepo.Settings(AppTheme.DARK, "")
    )

    private val rssParserRepository = RssParserRepository()
    val parsingState = rssParserRepository.parsingState

    fun fetchFeed(url: String) {
        viewModelScope.launch {
            rssParserRepository.parseUrl(url, null)
        }
    }

    fun save(channel: Channel, groupName: String) {
        viewModelScope.launch {
            try {
                repository.insertFeedUrl(channel, groupName)
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    fun updateCurrentGroup(s: String) {
        viewModelScope.launch(Dispatchers.IO) { userSettingsRepo.setCurrentGroup(s) }
    }

    private val _deltingState = MutableStateFlow(false)
    val deletingStateFlow = _deltingState.asStateFlow()

    fun deleteOldArticles(feedId: Long?, days: Int) {
        if (feedId == null || feedId <= 0 || days < 1) {
            Toast.makeText(getApplication(), "Invalid parameters $feedId and $days", Toast.LENGTH_SHORT).show()
            return
        }
        _deltingState.value = true
        val now = Instant.now()
        val threshold = now.minus(days.toLong(), ChronoUnit.DAYS).toEpochMilli()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removeOldArticles(feedId, threshold)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _deltingState.value = false
            }
        }
    }

    fun removeArticle(it: Long) {
        viewModelScope.launch {
            repository.deleteArticle(it)
        }
    }
}

