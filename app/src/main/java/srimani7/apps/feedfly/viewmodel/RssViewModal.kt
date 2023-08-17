package srimani7.apps.feedfly.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import srimani7.apps.feedfly.database.AppDatabase
import srimani7.apps.feedfly.database.dbErrorLog
import srimani7.apps.feedfly.database.dbInfoLog
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.feedfly.database.entity.FeedImage
import srimani7.apps.feedfly.rss.RssParserRepository
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.ParsingState
import srimani7.apps.rssparser.ParsingState.LastBuild
import srimani7.apps.rssparser.ParsingState.Processing
import srimani7.apps.rssparser.elements.Channel
import java.util.Date

class RssViewModal(feedId: Long, application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    val feedStateFlow = feedDao.getFeed(feedId).stateIn(viewModelScope, SharingStarted.Lazily, null)
    val groupNameFlow by lazy { feedDao.getGroups().stateIn(viewModelScope, SharingStarted.Lazily, emptyList()) }

    private val _uiState = MutableStateFlow<ArticlesUIState>(ArticlesUIState.Loading)
    val uiStateStateFlow = _uiState.asStateFlow()
    private var lastBuildDate: Date? = null

    private val rssParserRepository by lazy { RssParserRepository() }

    val groupedArticles = feedDao
        .getArticles(feedId)
        .transform { feedArticles ->
            emit(feedArticles.groupBy { DateParser.formatDate(it.pubDate) })
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            rssParserRepository.parsingState.collectLatest { parsingState ->
                when (parsingState) {
                    is ParsingState.Failure -> {
                        _uiState.value = ArticlesUIState.Failure(
                            parsingState.exception.message ?: "Please try again"
                        )
                    }

                    is ParsingState.Success -> {
                        val channel = parsingState.channel
                        if (channel.lastBuildDate == null) {
                            lastBuildDate = Date()
                            channel.lastBuildDate = lastBuildDate
                        } else lastBuildDate = channel.lastBuildDate
                        if (feedStateFlow.value != null) {
                            val fee = feedStateFlow.value?.copy(channel)!!
                            parseAndInsert(fee, channel)
                        }
                        _uiState.value = ArticlesUIState.COMPLETED
                    }

                    LastBuild -> {
                        lastBuildDate = feedStateFlow.value?.lastBuildDate
                        _uiState.value = ArticlesUIState.COMPLETED
                    }

                    Processing -> _uiState.value = ArticlesUIState.Loading
                    ParsingState.Completed -> {}
                }
            }
        }
    }

    fun parseXml(feed: Feed) {
        if (feed.lastBuildDate != null && lastBuildDate == feed.lastBuildDate) {
            _uiState.value = ArticlesUIState.COMPLETED
            return
        }
        info(feed.lastBuildDate.toString() + " " + lastBuildDate)
        load(feed)
    }

    private fun load(feed: Feed) {
        viewModelScope.launch(Dispatchers.IO) {
            rssParserRepository.parseUrl(feed.feedUrl, feed.lastBuildDate)
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

sealed class ArticlesUIState {
    object Loading : ArticlesUIState()
    object COMPLETED : ArticlesUIState()
    data class Failure(val message: String) : ArticlesUIState()
}