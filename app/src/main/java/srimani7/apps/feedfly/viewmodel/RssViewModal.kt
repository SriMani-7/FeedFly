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
import srimani7.apps.feedfly.core.data.repository.FeedGroupRepository
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.data.repository.PrivateSpaceRepository
import srimani7.apps.feedfly.core.data.repository.RssFeedRepository
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo
import javax.inject.Inject

@HiltViewModel
class RssViewModal @Inject constructor(
    labelRepository: LabelRepository,
    private val privateSpaceRepository: PrivateSpaceRepository,
    private val rssFeedRepository: RssFeedRepository,
    feedGroupRepository: FeedGroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val feedId: Long = savedStateHandle["id"] ?: -1

    val feedStateFlow =
        rssFeedRepository.getFeed(feedId).stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val _feed get() = feedStateFlow.value

    val groupNameFlow = feedGroupRepository.getGroups()
    val uiStateStateFlow = rssFeedRepository.uiState

    private val _articlesFlow = MutableStateFlow(emptyList<LabelledArticle>())
    val articles = _articlesFlow.asStateFlow()

    val articlesLabelsFlow = labelRepository.getArticleLabels(feedId)
    val selectedLabel = mutableStateOf<Long?>(null)

    init {
        applyLabelFilter(null)
    }

    private fun load() {
        _feed?.let {
            viewModelScope.launch(Dispatchers.IO) {
                rssFeedRepository.updateFeed(it)
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
                rssFeedRepository.deleteFeed(it.id)
            }
        }
    }

    fun refresh() = load()

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            rssFeedRepository.deleteArticle(articleId)
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
            rssFeedRepository.getFeedArticles(feedId, selectedLabel.value).collectLatest { articles ->
                _articlesFlow.update { articles }
            }
        }
    }

    fun updateFeedGroup(name: String) {
        _feed?.let {
            viewModelScope.launch {
                rssFeedRepository.updateFeedGroup(it.id, name)
            }
        }
    }
}