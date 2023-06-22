package com.ithoughts.mynaa.tsd.rss

sealed class ParsingState {
    object Loading : ParsingState()
    data class Success(val rss: Rss?) : ParsingState()
    data class Error(val message: String) : ParsingState()
}