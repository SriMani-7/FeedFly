package srimani7.apps.feedfly.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.Repository
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.data.repository.PrivateSpaceRepository
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.ParsingState.LastBuild
import srimani7.apps.rssparser.ParsingState.Processing
import srimani7.apps.rssparser.RssParserRepository
import srimani7.apps.rssparser.debugLog
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RssViewModal @Inject constructor(
    private val databaseRepo: Repository,
    labelRepository: LabelRepository,
    private val privateSpaceRepository: PrivateSpaceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val feedId: Long = savedStateHandle["id"] ?: -1

    val feedStateFlow =
        databaseRepo.getFeed(feedId).stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val _feed get() = feedStateFlow.value

    val groupNameFlow by lazy {
        databaseRepo.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    private val _uiState = MutableStateFlow<ArticlesUIState>(ArticlesUIState.COMPLETED)
    val uiStateStateFlow = _uiState.asStateFlow()
    private var lastBuildDate: Date? = null

    private val _articlesFlow = MutableStateFlow(emptyList<LabelledArticle>())
    val articles = _articlesFlow.asStateFlow()

    private val rssParserRepository by lazy { RssParserRepository() }
    val articlesLabelsFlow = labelRepository.getArticleLabels(feedId)
    val selectedLabel = mutableStateOf<Long?>(null)

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
                        _uiState.value = ArticlesUIState.LastBuild
                    }

                    Processing -> _uiState.value = ArticlesUIState.Loading
                    ParsingState.Completed -> {}
                }
            }
        }
        applyLabelFilter(null)
    }

    private fun load() {
        _feed?.let {
            viewModelScope.launch(Dispatchers.IO) {
                rssParserRepository.parseUrl(it.feedUrl, it.lastBuildDate)
            }
        }
    }

    companion object {
        fun info(any: Any) {
            Log.i("vrss_", any.toString())
        }
    }

    fun delete() {
        _feed?.let {
            viewModelScope.launch {
                databaseRepo.delete(it.id)
            }
        }
    }

    fun refresh() = load()

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            databaseRepo.deleteArticle(articleId)
        }
    }

    fun onMoveToPrivate(l: Long) {
        viewModelScope.launch {
            privateSpaceRepository.moveArticleToPrivate(l)
        }
    }

    private var preArticleJob: Job? = null
    fun applyLabelFilter(id: Long?) {
        selectedLabel.value = if (selectedLabel.value == id) null else id
        preArticleJob?.cancel()
        preArticleJob = viewModelScope.launch {
            databaseRepo.getFeedArticles(feedId, selectedLabel.value).collectLatest { articles ->
                debugLog("collecting filtered articles for $id")
                _articlesFlow.update { articles }
            }
        }
    }

    fun updateFeedGroup(name: String) {
        _feed?.let {
            viewModelScope.launch {
                databaseRepo.updateFeedGroup(it.id, name)
            }
        }
    }
}

sealed class ArticlesUIState(val message: String?) {
    data object Loading : ArticlesUIState(null)
    data object COMPLETED : ArticlesUIState(null)
    data object LastBuild : ArticlesUIState("You are up to date")
    class Failure(message: String?) : ArticlesUIState(message)
}