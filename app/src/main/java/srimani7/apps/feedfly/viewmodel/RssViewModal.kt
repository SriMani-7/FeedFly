package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.database.Repository
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.ParsingState.LastBuild
import srimani7.apps.rssparser.ParsingState.Processing
import srimani7.apps.rssparser.RssParserRepository
import srimani7.apps.rssparser.debugLog
import java.util.Date

class RssViewModal(private val feedId: Long, application: Application) :
    AndroidViewModel(application) {

    private val databaseRepo = Repository(application)

    val feedStateFlow =
        databaseRepo.getFeed(feedId).stateIn(viewModelScope, SharingStarted.Lazily, null)
    val groupNameFlow by lazy {
        databaseRepo.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    private val _uiState = MutableStateFlow<ArticlesUIState>(ArticlesUIState.Loading)
    val uiStateStateFlow = _uiState.asStateFlow()
    private var lastBuildDate: Date? = null

    private val _articlesFlow = MutableStateFlow(emptyList<LabelledArticle>())
    val articles = _articlesFlow.asStateFlow()

    private val rssParserRepository by lazy { RssParserRepository() }
    val articlesLabelsFlow = databaseRepo.getArticleLabels(feedId)
    val selectedLabel = mutableStateOf<Long?>(null)

    val groupedArticles = databaseRepo
        .getLabelledArticles(feedId)
        .transform { feedArticles ->
            emit(feedArticles.groupBy { DateParser.formatDate(it.publishedTime, false) })
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            rssParserRepository.parsingState.collectLatest { parsingState ->
                when (parsingState) {
                    is ParsingState.Failure -> {
                        _uiState.value = ArticlesUIState.Failure(
                            parsingState.exception.message ?: "Please try again"
                        )
                    }

                    is ParsingState.Success -> {
                        val channel = parsingState.channel
                        if (channel.lastBuildDate == null) {
                            lastBuildDate = Date()
                            channel.lastBuildDate = lastBuildDate
                        } else lastBuildDate = channel.lastBuildDate
                        if (feedStateFlow.value != null) {
                            feedStateFlow.value?.let {
                                databaseRepo.updateAndInsertArticles(it, channel)
                            }
                        }
                        _uiState.value = ArticlesUIState.COMPLETED
                    }

                    LastBuild -> {
                        lastBuildDate = feedStateFlow.value?.lastBuildDate
                        _uiState.value = ArticlesUIState.COMPLETED
                    }

                    Processing -> _uiState.value = ArticlesUIState.Loading
                    ParsingState.Completed -> {}
                }
            }
        }
    }

    fun parseXml(feed: Feed) {
        if (feed.lastBuildDate != null && lastBuildDate == feed.lastBuildDate) {
            _uiState.value = ArticlesUIState.COMPLETED
            return
        }
        info(feed.lastBuildDate.toString() + " " + lastBuildDate)
        load(feed)
    }

    private fun load(feed: Feed) {
        viewModelScope.launch(Dispatchers.IO) {
            rssParserRepository.parseUrl(feed.feedUrl, feed.lastBuildDate)
        }
    }

    companion object {
        fun info(any: Any) {
            Log.i("vrss_", any.toString())
        }
    }

    fun delete(feed: Feed?) {
        if (feed == null) return
        viewModelScope.launch {
            databaseRepo.delete(feed)
        }
    }

    fun refresh(feed: Feed?) {
        if (feed == null) return
        load(feed)
    }

    fun updateFeed(copy: Feed?) {
        if (copy == null) return
        viewModelScope.launch { databaseRepo.updateFeedUrl(copy) }
    }

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            databaseRepo.deleteArticle(articleId)
        }
    }

    fun onMoveToPrivate(l: Long) {
        viewModelScope.launch {
            databaseRepo.moveArticleToPrivate(l)
        }
    }

    private var preArticleJob: Job? = null
    fun applyLabelFilter(id: Long) {
        selectedLabel.value = if (selectedLabel.value == id) null else id
        preArticleJob?.cancel()
        preArticleJob = viewModelScope.launch {
            databaseRepo.getFeedArticles(feedId, selectedLabel.value).collectLatest { articles ->
                debugLog("collecting filtered articles for $id")
                _articlesFlow.update { articles }
            }
        }
    }
}

sealed class ArticlesUIState {
    object Loading : ArticlesUIState()
    object COMPLETED : ArticlesUIState()
    data class Failure(val message: String) : ArticlesUIState()
}