package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(application: Application, private val labelRepository: LabelRepository) : AndroidViewModel(application) {

    val labels by lazy { labelRepository.getAllLabels() }

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