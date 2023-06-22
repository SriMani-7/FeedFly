@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.ithoughts.mynaa.tsd.rss

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RssViewModal : ViewModel() {
    private val rssParser by lazy { RssParser() }
    private val okHttpWebService by lazy { OkHttpWebService() }
    private val _parsingState = MutableStateFlow<ParsingState>(ParsingState.Success(null))
    val parsingState: StateFlow<ParsingState> = _parsingState

    fun parseXml(url: String) {
        viewModelScope.launch {
            try {
                _parsingState.value = ParsingState.Loading
                val xmlData = okHttpWebService.getXMlString(url)
                val rss = xmlData?.let { rssParser.parseRss(xmlData) }
                if (rss != null) {
                    info(rss)
                    _parsingState.value = ParsingState.Success(rss)
                } else _parsingState.value = ParsingState.Error("Unknown response")
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

