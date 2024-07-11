package srimani7.apps.feedfly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import srimani7.apps.feedfly.core.data.Repository
import javax.inject.Inject

@HiltViewModel
class FeedGroupViewModel @Inject constructor(
    repository: Repository, savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val group: String = savedStateHandle["group"] ?: "Others"

    val name by mutableStateOf(group)
    val feeds = repository.getFeeds(group)
}