package com.ithoughts.mynaa.tsd.rss

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class RssParser {
    private val factory = DocumentBuilderFactory.newInstance()

    suspend fun parseRss(xml: InputStream): Rss = withContext(Dispatchers.IO) {
        val documentBuilder = factory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(xml))

        val rssElement = document.documentElement
        val channelElements = rssElement.getElementsByTagName("channel")

        val channels = mutableListOf<Rss.Channel>()

        if (channelElements.length > 0) {
            val channelElement = channelElements.item(0) as Element
            val channel = parseChannel(channelElement)
            channels.add(channel)
        }

        Rss(channels)
    }

    private fun parseChannel(channelElement: Element): Rss.Channel {
        val channel = Rss.Channel()

        channel.title = getNodeValue(channelElement, "title")
        channel.link = getNodeValue(channelElement, "link")
        channel.description = getNodeValue(channelElement, "description")
        channel.lastBuildDate = getNodeValue(channelElement, "lastBuildDate")

        val itemElements = channelElement.getElementsByTagName("item")
        for (i in 0 until itemElements.length) {
            val itemElement = itemElements.item(i) as Element
            val item = parseItem(itemElement)
            channel.list.add(item)
        }

        return channel
    }

    private fun parseItem(itemElement: Element): Rss.Channel.Item {
        val item = Rss.Channel.Item()

        item.title = getNodeValue(itemElement, "title")
        item.link = getNodeValue(itemElement, "link")
        item.category = getNodeValue(itemElement, "category")
        item.guid = getNodeValue(itemElement, "guid")

        return item
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
}