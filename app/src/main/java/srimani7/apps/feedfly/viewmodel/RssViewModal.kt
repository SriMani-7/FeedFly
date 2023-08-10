package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.dbErrorLog
import srimani7.apps.feedfly.database.dbInfoLog
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.OkHttpWebService
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.ParsingState.LastBuild
import srimani7.apps.rssparser.ParsingState.Processing
import srimani7.apps.rssparser.RssParser
import srimani7.apps.rssparser.elements.Channel
import java.util.Date

class RssViewModal(feedId: Long, application: Application) : AndroidViewModel(application) {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    val feed = feedDao.getFeed(feedId)
    val groupNameFlow by lazy { feedDao.getGroups() }

    private val _parsingState = MutableStateFlow<ParsingState>(Processing)
    val parsingState: StateFlow<ParsingState> = _parsingState
    private var lastBuildDate: Date? = null

    val groupedArticles = feedDao
        .getArticles(feedId)
        .transform { feedArticles ->
            emit(feedArticles.groupBy { DateParser.formatDate(it.pubDate) })
        }

    fun parseXml(feed: Feed) {
        if (feed.lastBuildDate != null && lastBuildDate == feed.lastBuildDate) {
            _parsingState.value = LastBuild
            return
        }
        info(feed.lastBuildDate.toString() + " " + lastBuildDate)
        load(feed)
    }

    private fun load(feed: Feed) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _parsingState.value = Processing
                val streamResult = okHttpWebService.inputStreamResult(feed.feedUrl)
                val state = streamResult.getOrThrow().let {
                    rssParser.parse(it, feed.lastBuildDate)
                }
                when (state) {
                    is ParsingState.Failure -> {
                        state.exception.printStackTrace()
                        _parsingState.value = state
                    }

                    is ParsingState.Success -> {
                        if (state.channel.lastBuildDate == null) {
                            lastBuildDate = Date()
                            state.channel.lastBuildDate = lastBuildDate
                        } else lastBuildDate = state.channel.lastBuildDate
                        val fee = feed.copy(state.channel)
                        parseAndInsert(fee, state.channel)
                        _parsingState.value = ParsingState.Completed
                    }

                    Processing, ParsingState.Completed -> _parsingState.value = state
                    LastBuild -> {
                        lastBuildDate = feed.lastBuildDate
                        _parsingState.value = state
                    }
                }
            } catch (e: Exception) {
                _parsingState.value = ParsingState.Failure(e)
                e.printStackTrace()
            }
        }
    }

    fun updateArticle(id: Long, pinned: Boolean) {
        viewModelScope.launch { feedDao.updateArticlePin(id, pinned) }
    }

    companion object {
        fun info(any: Any) {
            Log.i("vrss_", any.toString())
        }
    }

    private suspend fun parseAndInsert(feed: Feed, channel: Channel) = withContext(Dispatchers.IO) {
        feedDao.updateFeedUrl(feed)
        channel.image?.let {
            launch {
                feedDao.insert(FeedImage(it, feed.id))
                cancel()
            }
        }
        channel.items.forEach { channelItem ->
            val article = ArticleItem(channelItem, feed.id)
            val rowId = feedDao.insert(article)
            val enclosure = channelItem.enclosure
            if (enclosure != null && rowId != -1L) launch {
                try {
                    feedDao.insertArticleMedia(
                        mediaSize = enclosure.length,
                        mediaType = enclosure.type,
                        url = enclosure.url,
                        articleLink = article.link,
                        articleTitle = article.title
                    )
                    dbInfoLog("Inserted media")
                } catch (e: Exception) {
                    dbErrorLog("For the $enclosure ${article.title} ${article.link}", e)
                } finally {
                    cancel()
                }
            }
        }
    }

    fun delete(feed: Feed?) {
        if (feed == null) return
        viewModelScope.launch {
            feedDao.delete(feed)
        }
    }

    fun refresh(feed: Feed?) {
        if (feed == null) return
        load(feed)
    }

    fun updateFeed(copy: Feed?) {
        if (copy == null) return
        viewModelScope.launch { feedDao.updateFeedUrl(copy) }
    }
}
