package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.data.repository.impl.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModal @Inject constructor(
    private val repository: Repository,
    private val labelRepository: LabelRepository, application: Application
) : AndroidViewModel(application) {

    val feedGroupsFlow = repository.getFeedGroups()
    val pinnedLabelsFlow = labelRepository.getPinnedLabels()

    val labels by lazy { labelRepository.getAllLabels() }

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

