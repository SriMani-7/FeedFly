package com.ithoughts.mynaa.tsd.rss.vm

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.ithoughts.mynaa.tsd.rss.OkHttpWebService
import com.ithoughts.mynaa.tsd.rss.RssParser
import com.ithoughts.mynaa.tsd.rss.db.AppDatabase

class FeedsViewModal(application: Application, private val groupName: String?) : AndroidViewModel(application) {
    private val feedDao by lazy { AppDatabase.getInstance(application).feedDao() }

    fun allFeeds() = feedDao.getAllFeedUrls(groupName)
}