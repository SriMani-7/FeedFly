package srimani7.apps.feedfly.rss

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import srimani7.apps.rssparser.OkHttpWebService
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.RssParser
import java.util.Date

class RssParserRepository {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }

    private val _parsingState = MutableStateFlow<ParsingState>(ParsingState.Completed)
    val parsingState: StateFlow<ParsingState> = _parsingState

    suspend fun parseUrl(url: String, lastBuildDate: Date?) {
        try {
            _parsingState.value = ParsingState.Processing
            val streamResult = okHttpWebService.inputStreamResult(url)
            val state = streamResult.getOrThrow().let {
                rssParser.parse(it, lastBuildDate, url)
            }
            if (state is ParsingState.Failure) state.exception.printStackTrace()
            _parsingState.value = state
        } catch (e: Exception) {
            e.printStackTrace()
            _parsingState.value = ParsingState.Failure(e)
        }
    }
}