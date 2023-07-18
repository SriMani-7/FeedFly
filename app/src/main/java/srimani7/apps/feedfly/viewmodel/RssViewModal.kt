package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.ArticleMedia
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.OkHttpWebService
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.ParsingState.Completed
import srimani7.apps.rssparser.ParsingState.Failure
import srimani7.apps.rssparser.ParsingState.LastBuild
import srimani7.apps.rssparser.ParsingState.Processing
import srimani7.apps.rssparser.ParsingState.Success
import srimani7.apps.rssparser.RssParser
import srimani7.apps.rssparser.elements.Channel
import java.util.Date

class RssViewModal(feedId: Long, application: Application) : AndroidViewModel(application) {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    val feed = feedDao.getFeed(feedId)

    private val _parsingState = MutableStateFlow<ParsingState>(Processing)
    val parsingState: StateFlow<ParsingState> = _parsingState
    private var lastBuildDate: Date? = null

    val groupedArticles = feedDao
        .getFeedArticles(feedId)
        .transform { feedArticles ->
            emit(feedArticles.groupBy { DateParser.formatDate(it.pubDate) })
        }

    fun parseXml(feed: Feed) {
        if (feed.lastBuildDate != null && lastBuildDate == feed.lastBuildDate) {
            _parsingState.value = LastBuild
            return
        }
        info(feed.lastBuildDate.toString()+" "+lastBuildDate)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _parsingState.value = Processing
                val streamResult = okHttpWebService.inputStreamResult(feed.feedUrl)
                val state = streamResult.getOrThrow().let {
                    rssParser.parse(it, feed.lastBuildDate)
                }
                when (state) {
                    is Failure -> {
                        state.exception.printStackTrace()
                        _parsingState.value = state
                    }

                    is Success -> {
                        if (state.channel.lastBuildDate == null) {
                            lastBuildDate = Date()
                            state.channel.lastBuildDate = lastBuildDate
                        } else lastBuildDate = state.channel.lastBuildDate
                        val fee = feed.copy(state.channel)
                        parseAndInsert(fee, state.channel)
                        _parsingState.value = Completed
                    }

                    LastBuild, Processing, Completed -> _parsingState.value = state
                }
            } catch (e: Exception) {
                _parsingState.value = Failure(e)
                e.printStackTrace()
            }
        }
    }

    fun updateArticle(articleItem: ArticleItem) {
        viewModelScope.launch { feedDao.updateArticle(articleItem) }
    }

    companion object {
        fun info(any: Any) {
            Log.i("rss_", any.toString())
        }
    }

    private suspend fun parseAndInsert(feed: Feed, channel: Channel) {
        feedDao.updateFeedUrl(feed)
        channel.image?.let {
            feedDao.insertOrUpdate(FeedImage(it, feed.id))
        }
        val articles = channel.items.associate {
            val article = ArticleItem(it, feed.id)
            val media = it.enclosure?.let { it1 -> ArticleMedia(it1, article.id) }
            article to media
        }

        articles.forEach { (articleItem, articleMedia) ->
            feedDao.insertOrUpdate(articleItem)
            articleMedia?.let { feedDao.insertOrUpdate(articleMedia) }
        }
    }
}
