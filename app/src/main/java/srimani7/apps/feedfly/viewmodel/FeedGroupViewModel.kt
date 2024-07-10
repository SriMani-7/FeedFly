package srimani7.apps.feedfly.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import srimani7.apps.feedfly.core.data.Repository

class FeedGroupViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val group: String = savedStateHandle["group"] ?: "Others"
    private val repository = Repository(application)

    val name by mutableStateOf(group)
    val feeds = repository.getFeeds(group)
}