package srimani7.apps.feedfly.rss

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class OkHttpWebService {

    private val service = srimani7.apps.rssparser.OkHttpWebService()

    suspend fun getXMlString(url: String): InputStream = withContext(Dispatchers.IO) {
        val result = service.inputStreamResult(url)
        return@withContext result.getOrElse { throw it }
    }
}