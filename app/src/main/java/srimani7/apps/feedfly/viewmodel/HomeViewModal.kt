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
import srimani7.apps.feedfly.data.AppTheme
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.rss.RssParserRepository
import srimani7.apps.rssparser.elements.Channel
import java.time.Instant
import java.time.temporal.ChronoUnit

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    val allFeedsFlow by lazy {
        feedDao.getAllFeeds().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
    val favoriteArticles by lazy { feedDao.getFavoriteFeedArticles() }
    val groupNameFlow by lazy {
        feedDao.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

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

    fun updateSettings(newTheme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) { userSettingsRepo.updateSettings(newTheme) }
    }

    fun updateArticle(id: Long, pinned: Boolean) {
        viewModelScope.launch { feedDao.updateArticlePin(id, pinned) }
    }

    fun save(channel: Channel, groupName: String) {
        viewModelScope.launch {
            try {
                feedDao.insertFeedUrl(Feed(channel, groupName))
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
                feedDao.removeOldArticles(feedId, threshold)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _deltingState.value = false
            }
        }
    }
}

