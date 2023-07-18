package srimani7.apps.rssparser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import srimani7.apps.rssparser.elements.Channel
import srimani7.apps.rssparser.elements.ChannelImage
import srimani7.apps.rssparser.elements.ChannelItem
import srimani7.apps.rssparser.elements.ItemEnclosure
import java.io.IOException
import java.io.InputStream
import java.util.Date

class RssParser {
    private val factory by lazy { XmlPullParserFactory.newInstance() }

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun parse(inputStream: InputStream, lastBuild: Date?): ParsingState = withContext(Dispatchers.IO) {
        inputStream.use {
            val parser: XmlPullParser = factory.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return@withContext reedRss(parser, lastBuild)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun reedRss(parser: XmlPullParser, lastBuild: Date?): ParsingState {
        var channel: ParsingState? = null
        parser.readTagChildren("rss") {
            if (parser.name == "channel") {
                channel = readChannel(parser, lastBuild)
                return@readTagChildren
            } else parser.skip()
        }
        return channel ?: ParsingState.Failure(Exception("Parsing issue"))
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readChannel(parser: XmlPullParser, lastBuild: Date?): ParsingState {
        val channel = Channel()
        val items = mutableListOf<ChannelItem>()
        var parsingState: ParsingState? = null
        parser.readTagChildren("channel") {
            when (parser.name) {
                "title" -> channel.title = parser.readText("title")
                "description" -> channel.description = parser.readText("description")
                "link" -> channel.link = parser.readText("link")
                "lastBuildDate" -> {
                    val buildDate = DateParser.parseDate(parser.readText("lastBuildDate"))
                    if (lastBuild != null && lastBuild == buildDate) {
                        parsingState = ParsingState.LastBuild
                        return@readTagChildren
                    }
                    channel.lastBuildDate = buildDate
                }

                "language" -> channel.language = parser.readText("language")
                "managingEditor" -> channel.managingEditor = parser.readText("managingEditor")
                "copyright" -> channel.copyright = parser.readText("copyright")
                "image" -> channel.image = readImage(parser)
                "item" -> items.add(readItem(parser))
                else -> parser.skip()
            }
        }
        return parsingState ?: ParsingState.Success(channel.apply { this.items = items })
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): ChannelItem {
        val item = ChannelItem()
        val mutableList = mutableListOf<String>()
        parser.readTagChildren("item") {
            when (parser.name) {
                "author" -> item.author = parser.readText("author")
                "title" -> item.title = parser.readText("title")
                "description" -> item.description = parser.readText("description")
                "link" -> item.link = parser.readText("link")
                "pubDate" -> item.pubDate = parser.readText("pubDate")
                "enclosure" -> item.enclosure = readEnclosure(parser)
                "category" -> parser.readText("category")?.let { mutableList.add(it) }
                else -> parser.skip()
            }
        }
        return item.apply { categories = mutableList }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readImage(parser: XmlPullParser): ChannelImage {
        val channelImage = ChannelImage()
        parser.readTagChildren("image") {
            when (parser.name) {
                "title" -> channelImage.title = parser.readText("title")
                "link" -> channelImage.link = parser.readText("link")
                "url" -> channelImage.url = parser.readText("url")
                "description" -> channelImage.description = parser.readText("description")
                "height" -> channelImage.height = parser.readText("height")?.toIntOrNull()
                "width" -> channelImage.width = parser.readText("width")?.toIntOrNull()
                else -> parser.skip()
            }
        }
        return channelImage
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEnclosure(parser: XmlPullParser): ItemEnclosure {
        parser.require(XmlPullParser.START_TAG, null, "enclosure")
        val length: Long? = parser.getAttributeValue(null, "length").toLongOrNull()
        val type: String? = parser.getAttributeValue(null, "type")
        val url: String? = parser.getAttributeValue(null, "url")
//        parser.require(XmlPullParser.END_TAG, null, "enclosure")
        return ItemEnclosure(length, type, url)
    }
}