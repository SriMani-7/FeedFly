package srimani7.apps.rssparser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream

class OkHttpWebService {

    private val client = OkHttpClient()

    suspend fun inputStreamResult(url: String): Result<InputStream> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return@withContext try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val contentType = response.headers["Content-Type"]
                val isXmlContent = acceptedMimeTypes.any { contentType?.contains(it) == true }
                if (isXmlContent) getInputStream(url)
                else Result.failure(IOException("Invalid content type : $contentType"))
            } else Result.failure(OkHttpException(response.code, response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getInputStream(url: String): Result<InputStream> {
        val request = Request.Builder().url(url).get().build()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()
                    ?.let { Result.success(it) }
                    ?: Result.failure(IOException("Empty response body"))
            } else Result.failure(OkHttpException(response.code, response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val RSS_XML = "application/rss+xml"
        const val APPLICATION_XML = "application/xml"
        const val TEXT_XML = "text/xml"

        val acceptedMimeTypes = listOf(
            RSS_XML, TEXT_XML, APPLICATION_XML
        )
    }
}

class OkHttpException(code: Int, message: String?) : Exception("HTTP $code: $message")
