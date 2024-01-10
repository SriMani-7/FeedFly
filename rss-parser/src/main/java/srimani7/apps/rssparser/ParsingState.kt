package srimani7.apps.rssparser

import srimani7.apps.rssparser.elements.Channel

sealed class ParsingState {
    object Processing : ParsingState()
    object LastBuild : ParsingState()
    object Completed: ParsingState()
    data class Success(val channel: Channel) : ParsingState()
    data class Failure(val exception: Exception) : ParsingState()
}