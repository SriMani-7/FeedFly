package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.widget.Toast
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
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.rss.OkHttpWebService
import srimani7.apps.feedfly.rss.RssParser

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val userSettingsRepo by lazy { UserSettingsRepo(application) }
    private val okHttpWebService by lazy { OkHttpWebService() }
    val groupsFlow by lazy { feedDao.getAllGroups() }
    val otherFeeds by lazy { feedDao.getOtherFeeds() }
    val favoriteArticles by lazy { feedDao.getFavoriteFeedArticles() }

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

    fun updateArticle(articleItem: ArticleItem) {
        viewModelScope.launch { feedDao.updateArticle(articleItem) }
    }
}

