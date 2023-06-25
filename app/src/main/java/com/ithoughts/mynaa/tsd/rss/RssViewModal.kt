package com.ithoughts.mynaa.tsd.rss

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ithoughts.mynaa.tsd.rss.db.AppDatabase
import com.ithoughts.mynaa.tsd.rss.db.Feed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RssViewModal(feedId: Long, application: Application) : AndroidViewModel(application) {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    val feedArticles = feedDao.getFeedArticles(feedId)

    private val _parsingState = MutableStateFlow<ParsingState>(ParsingState.Success)
    val parsingState: StateFlow<ParsingState> = _parsingState
    var lastBuildDate: String? = null

    init {
        viewModelScope.launch {
            feedArticles.collect {
                if (it.feed.lastBuildDate == null || lastBuildDate != it.feed.lastBuildDate) {
                    parseXml(it.feed)
                }
            }
        }
    }

    private fun parseXml(feed: Feed) {
        viewModelScope.launch {
            try {
                _parsingState.value = ParsingState.Loading
                val xmlData = okHttpWebService.getXMlString(feed.feedUrl)
                val feedArticle = xmlData?.let { rssParser.parseRss(feed, xmlData) }
                if (feedArticle != null) {
                    info(feedArticle)
                    feedDao.insertFeedWithArticles(feedArticle.feed, feedArticle.articles)
                    _parsingState.value = ParsingState.Success
                    lastBuildDate = feedArticle.feed.lastBuildDate
                }
                else {
                    _parsingState.value = ParsingState.Error("Articles are in on date")
                    lastBuildDate = feed.lastBuildDate
                }
            } catch (e: Exception) {
                _parsingState.value = ParsingState.Error(e.message ?: "Unknown error")
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun info(any: Any) {
            Log.i("rss_", any.toString())
        }
    }
}