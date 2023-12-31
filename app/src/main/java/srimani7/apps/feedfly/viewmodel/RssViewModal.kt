package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.rss.DateParser
import srimani7.apps.feedfly.rss.OkHttpWebService
import srimani7.apps.feedfly.rss.ParsingState
import srimani7.apps.feedfly.rss.RssParser
import java.util.Date

class RssViewModal(feedId: Long, application: Application) : AndroidViewModel(application) {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    val feed = feedDao.getFeed(feedId)

    private val _parsingState = MutableStateFlow<ParsingState>(ParsingState.Success)
    val parsingState: StateFlow<ParsingState> = _parsingState
    private var lastBuildDate: Date? = null

    val groupedArticles = feedDao
        .getFeedArticles(feedId)
        .transform { feedArticles ->
            emit(feedArticles.groupBy { DateParser.formatDate(it.pubDate) })
        }

    init {
        viewModelScope.launch {
            feed.collect { feed ->
                if (feed.lastBuildDate == null || lastBuildDate != feed.lastBuildDate) {
                    parseXml(feed)
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
                    info("Number of articles fetched for ${feed.feedUrl} : " + feedArticle.articles.size)
                    feedDao.updateFeedUrl(feedArticle.feed)
                    try {
                        feedDao.insertFeedArticles(feedArticle.articles)
                    } catch (e: SQLiteConstraintException) {
                        e.printStackTrace()
                    }
                    _parsingState.value = ParsingState.Success
                    lastBuildDate = feedArticle.feed.lastBuildDate
                } else {
                    _parsingState.value = ParsingState.Error("Articles are in on date")
                    lastBuildDate = feed.lastBuildDate
                }
            } catch (e: Exception) {
                _parsingState.value = ParsingState.Error(e.message ?: "Unknown error")
                e.printStackTrace()
            }
        }
    }

    fun updateArticle(articleItem: ArticleItem) {
        viewModelScope.launch { feedDao.updateArticle(articleItem)}
    }

    companion object {
        fun info(any: Any) {
            Log.i("rss_", any.toString())
        }
    }
}
