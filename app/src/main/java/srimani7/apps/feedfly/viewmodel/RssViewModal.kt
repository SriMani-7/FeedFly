package srimani7.apps.feedfly.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.data.repository.PrivateSpaceRepository
import srimani7.apps.feedfly.core.data.repository.RssFeedRepository
import srimani7.apps.feedfly.core.data.repository.impl.Repository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RssViewModal @Inject constructor(
    databaseRepo: Repository,
    labelRepository: LabelRepository,
    private val privateSpaceRepository: PrivateSpaceRepository,
    private val rssFeedRepository: RssFeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val feedId: Long = savedStateHandle["id"] ?: -1

    val feedStateFlow =
        rssFeedRepository.getFeed(feedId).stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val _feed get() = feedStateFlow.value

    val groupNameFlow = databaseRepo.getGroups()
    val uiStateStateFlow = rssFeedRepository.uiState

    private val _selectedLabel = MutableStateFlow<Long?>(null)
    val selectedLabel = _selectedLabel.asStateFlow()

    val articles = selectedLabel.flatMapLatest {  label ->
        rssFeedRepository.getFeedArticles(feedId, label)
    }

    val articlesLabelsFlow = labelRepository.getArticleLabels(feedId)

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

    fun applyLabelFilter(id: Long?) {
        _selectedLabel.update { id }
    }

    fun updateFeedGroup(name: String) {
        _feed?.let {
            viewModelScope.launch {
                rssFeedRepository.updateFeedGroup(it.id, name)
            }
        }
    }
}