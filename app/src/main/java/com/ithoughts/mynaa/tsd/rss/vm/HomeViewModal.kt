package com.ithoughts.mynaa.tsd.rss.vm

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ithoughts.mynaa.tsd.rss.OkHttpWebService
import com.ithoughts.mynaa.tsd.rss.RssParser
import com.ithoughts.mynaa.tsd.rss.db.AppDatabase
import kotlinx.coroutines.launch

class HomeViewModal(application: Application) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    val groupsFlow by lazy { feedDao.getAllGroups() }

    private val rssParser by lazy { RssParser() }
    var isLoading by mutableStateOf(false)

    fun insertFeed(it: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val inputStream = okHttpWebService.getXMlString(it)
                val feed = inputStream?.let { it1 -> rssParser.parseFeed(it, it1) }
                isLoading = if (feed != null) {
                    feedDao.insertFeedUrl(feed)
                    false
                } else {
                    Toast.makeText(getApplication(), "Unable to parse url", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } catch (e: Exception) {
                isLoading = false
                e.printStackTrace()
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}