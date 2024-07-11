package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.Repository
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.rssparser.RssParserRepository
import srimani7.apps.rssparser.elements.Channel
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModal @Inject constructor(
    private val repository: Repository,
    private val labelRepository: LabelRepository, application: Application
) : AndroidViewModel(application) {
    val groupNameFlow by lazy {
        repository.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    val feedGroupsFlow = repository.getFeedGroups()
    val pinnedLabelsFlow = labelRepository.getPinnedLabels()

    private val rssParserRepository = RssParserRepository()
    val parsingState = rssParserRepository.parsingState

    val labels by lazy { labelRepository.getAllLabels() }

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

    private val _deletingState = MutableStateFlow(false)
    val deletingStateFlow = _deletingState.asStateFlow()

    fun deleteOldArticles(feedId: Long?, days: Int) {
        if (feedId == null || feedId <= 0 || days < 1) {
            Toast.makeText(
                getApplication(),
                "Invalid parameters $feedId and $days",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        _deletingState.value = true
        val now = Instant.now()
        val threshold = now.minus(days.toLong(), ChronoUnit.DAYS).toEpochMilli()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removeOldArticles(feedId, threshold)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _deletingState.value = false
            }
        }
    }

    fun removeArticle(it: Long) {
        viewModelScope.launch {
            repository.deleteArticle(it)
        }
    }

    fun updateArticleLabel(articleId: Long, labelId: Long) {
        viewModelScope.launch { labelRepository.updateArticleLabel(articleId, labelId) }
    }

    fun removeArticleLabel(articleId: Long) {
        viewModelScope.launch { labelRepository.removeArticleLabel(articleId) }
    }

    fun addLabel(it: String) {
        viewModelScope.launch { labelRepository.addLabel(it) }
    }
}

