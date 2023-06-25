package com.ithoughts.mynaa.tsd.rss

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    private val pubDateFormats = listOf(
        "EEE, dd MMM yyyy HH:mm:ss zzz",
        "EEE, dd MMM yyyy HH:mm zzz",
        "EEE, dd MMM yyyy HH:mm zzz",
    )

    private fun parsePubDateStringToDate(value: String): Date? {
        for (dateFormat in pubDateFormats) {
            try {
                val articleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
                return articleDateFormat.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return Date()
    }

}

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