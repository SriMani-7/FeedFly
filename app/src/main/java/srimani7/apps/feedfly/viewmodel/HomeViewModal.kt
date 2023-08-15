package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.data.AppTheme
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.navigation.HomeFilter
import srimani7.apps.feedfly.rss.RssParserRepository
import srimani7.apps.rssparser.elements.Channel

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    val allFeedsFlow by lazy { feedDao.getAllFeeds() }
    val favoriteArticles by lazy { feedDao.getFavoriteFeedArticles() }
    val groupNameFlow by lazy { feedDao.getGroups() }

    var isLoading by mutableStateOf(false)
    val appThemeState = userSettingsRepo.appThemeFlow(viewModelScope)
    var currentFilter by mutableStateOf<HomeFilter>(HomeFilter.ALL)
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

    fun save(channel: Channel, groupName: String?) {
        viewModelScope.launch {
            feedDao.insertFeedUrl(Feed(channel, groupName))
        }
    }

}

