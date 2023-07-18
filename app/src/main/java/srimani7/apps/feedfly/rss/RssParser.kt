package srimani7.apps.feedfly.rss

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import srimani7.apps.feedfly.database.entity.Feed
import java.io.InputStream

class RssParser {
    private val xmlPullParserFactory = XmlPullParserFactory.newInstance()

    suspend fun parseFeed(url: String, inputStream: InputStream): Feed? =
        withContext(Dispatchers.IO) {
            val parser = xmlPullParserFactory.newPullParser()
            inputStream.reader().use {
                parser.setInput(it)
                val map = mutableMapOf<String, String>()
                map["feedUrl"] = url
                debug("Feed : $url")
                var eventType: Int
                var tagName: String? = null
                do {
                    eventType = parser.next()
                    when (parser.eventType) {
                        XmlPullParser.START_TAG -> {
                            tagName =
                                if (parser.depth == 3 && parser.name in Feed.tags()) parser.name
                                else null
                        }

                        XmlPullParser.END_TAG -> {
                            if (parser.name == "channel") {
                                debug("Breaking loop at end of CHANNEL")
                                break
                            }
                        }

                        XmlPullParser.TEXT -> {
                            val text = parser.text.trim()
                            if (text.isNotBlank()) {
                                if (parser.depth == 3 && tagName != null) map[tagName] = text
                                if (map.size == Feed.tags().size) {
                                    debug("Breaking parsing loop")
                                    break
                                }
                            }
                        }
                    }
                } while (eventType != XmlPullParser.END_DOCUMENT)
                debug("Feed : $map")
                if (map.size < 2) return@use null
                return@use Feed(map)
            }
        }

    private fun debug(message: Any?) {
        Log.d("parse_xml", message.toString())
    }
}