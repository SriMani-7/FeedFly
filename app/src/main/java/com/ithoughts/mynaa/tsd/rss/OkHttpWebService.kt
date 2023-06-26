package com.ithoughts.mynaa.tsd.rss

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

class OkHttpWebService {

    private val client = OkHttpClient()

    suspend fun getXMlString(url: String): InputStream? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = client.newCall(request).execute()
            return@withContext response.body?.byteStream()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}