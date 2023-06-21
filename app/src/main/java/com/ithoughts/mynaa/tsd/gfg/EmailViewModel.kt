package com.ithoughts.mynaa.tsd.gfg

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmailViewModel : ViewModel() {
    private val _messagesState = MutableStateFlow(emptyList<EmailMessage>())
    val messagesState: StateFlow<List<EmailMessage>> = _messagesState.asStateFlow()

    init {
        _messagesState.update { sampleMessages() }
    }

    fun refresh() {
        _messagesState.update {
            sampleMessages()
        }
    }

    fun removeItem(currentItem: EmailMessage) {
        _messagesState.update {
            val toMutableList = it.toMutableList()
            toMutableList.remove(currentItem)
            toMutableList
        }
    }

    private fun sampleMessages() = listOf(
        EmailMessage("John Doe", "Hello"),
        EmailMessage("Alice", "Hey there! How's it going?"),
        EmailMessage("Bob", "I just discovered a cool new programming language!"),
        EmailMessage("Geek", "Have you seen the latest tech news? It's fascinating!"),
        EmailMessage("Mark", "Let's grab a coffee and talk about coding!"),
        EmailMessage("Cyan", "I need help with a coding problem. Can you assist me?"),
    )
}

data class EmailMessage(
    val sender: String,
    val message: String
)

