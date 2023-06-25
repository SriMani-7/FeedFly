package com.ithoughts.mynaa.tsd.rss

sealed class ParsingState {
    object Loading : ParsingState()
    object Success : ParsingState()
    data class Error(val message: String) : ParsingState()
}