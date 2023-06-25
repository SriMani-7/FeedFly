package com.ithoughts.mynaa.tsd.rss

import android.util.Log
import com.ithoughts.mynaa.tsd.rss.db.ArticleItem
import com.ithoughts.mynaa.tsd.rss.db.Feed
import com.ithoughts.mynaa.tsd.rss.db.FeedArticle
import com.ithoughts.mynaa.tsd.rss.db.MutableArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class RssParser {
    private val factory = DocumentBuilderFactory.newInstance()
    private val xmlPullParserFactory = XmlPullParserFactory.newInstance()

    suspend fun parseRss(feed: Feed, xml: InputStream): FeedArticle? = withContext(Dispatchers.IO) {
        val documentBuilder = factory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(xml))
        val rssElement = document.documentElement
        val lastBuildDate = getNodeValue(rssElement, "lastBuildDate")
        if (feed.lastBuildDate == lastBuildDate) return@withContext null

        val articles = mutableListOf<ArticleItem>()
        val itemElements = rssElement.getElementsByTagName("item")
        for (i in 0 until itemElements.length) {
            val itemElement = itemElements.item(i) as Element
            val mutableArticle = MutableArticle(feed.id)
            mutableArticle.apply {
                title = getNodeValue(itemElement, "title")
                link = getNodeValue(itemElement, "link")
                category = getNodeValue(itemElement, "category")
                description = getNodeValue(itemElement, "description")
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