package srimani7.apps.feedfly.rss

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import srimani7.apps.feedfly.database.FeedArticle
import srimani7.apps.feedfly.database.MutableArticle
import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed
import srimani7.apps.rssparser.DateParser
import java.io.InputStream
import java.util.Date
import javax.xml.parsers.DocumentBuilderFactory

class RssParser {
    private val factory = DocumentBuilderFactory.newInstance()
    private val xmlPullParserFactory = XmlPullParserFactory.newInstance()

    suspend fun parseRss(feed: Feed, xml: InputStream): FeedArticle? = withContext(Dispatchers.IO) {
        val documentBuilder = factory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(xml))
        val rssElement = document.documentElement
        var lastBuildDate = DateParser.parseDate(getNodeValue(rssElement, "lastBuildDate"))
        if (lastBuildDate != null && feed.lastBuildDate == lastBuildDate) return@withContext null
        lastBuildDate = lastBuildDate ?: Date()
        val articles = mutableListOf<ArticleItem>()
        val itemElements = rssElement.getElementsByTagName("item")
        val fetchDate = Date()
        for (i in 0 until itemElements.length) {
            val itemElement = itemElements.item(i) as Element
            val mutableArticle = MutableArticle(feed.id)
            mutableArticle.apply {
                title = getNodeValue(itemElement, "title")
                link = getNodeValue(itemElement, "link")
                category = getNodeValue(itemElement, "category")
                description = getNodeValue(itemElement, "description")
                lastFetched = fetchDate
                pubDate = DateParser.parseDate(getNodeValue(itemElement, "pubDate"))
            }
            articles.add(mutableArticle.immutable())
        }
        return@withContext FeedArticle(
            feed = feed.copy(lastBuildDate = lastBuildDate),
            articles = articles
        )
    }

    private fun getNodeValue(element: Element, tagName: String): String {
        val nodeList = element.getElementsByTagName(tagName)
        if (nodeList.length > 0) {
            val node = nodeList.item(0)
            if (node.firstChild != null) {
                return node.firstChild.nodeValue
            }
        }
        return ""
    }

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