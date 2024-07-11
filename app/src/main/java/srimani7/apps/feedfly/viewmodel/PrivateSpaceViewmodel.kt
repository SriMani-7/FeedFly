package srimani7.apps.feedfly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.data.PrivateSpaceRepo
import srimani7.apps.feedfly.core.model.PrivateArticle
import javax.inject.Inject

@HiltViewModel
class PrivateSpaceViewmodel @Inject constructor(
    private val repository: PrivateSpaceRepo
) : ViewModel() {

    val groupsFlow = repository.groups
    var selectedGroup by mutableStateOf("Others")
        private set

    private val _articlesFlow = MutableStateFlow(emptyList<PrivateArticle>())
    val articlesFlow = _articlesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            changeGroup("Others")
        }
    }

    private var gJob: Job? = null
    fun changeGroup(group: String) {
        selectedGroup = group
        gJob?.cancel()
        gJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getPrivateArticles(group).collectLatest { articles ->
                _articlesFlow.update { articles }
            }
        }

    }

    fun unLockArticle(it: Long) {
        viewModelScope.launch {
            repository.unLockArticle(it)
        }
    }
}