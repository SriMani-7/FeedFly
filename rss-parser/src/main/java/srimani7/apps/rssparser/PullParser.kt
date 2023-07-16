package srimani7.apps.rssparser

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

fun debugLog(message: String) {
    Log.d("rss_parser", message)
}

@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.readTagChildren(tagName: String, nameSpace: String? = null, body: () -> Unit) {
    require(XmlPullParser.START_TAG, nameSpace, tagName)
    debugLog("parsing $tagName -----")
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }
        debugLog("child $name")
        body()
    }
    debugLog("parsing $tagName completed -----")
}

@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.readText(tagName: String, nameSpace: String? = null): String {
    require(XmlPullParser.START_TAG, nameSpace, tagName)
    var result = ""
    if (next() == XmlPullParser.TEXT) {
        result = text
        nextTag()
    }
    require(XmlPullParser.END_TAG, nameSpace, tagName)
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
internal fun XmlPullParser.skip() {
    if (eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth2 = 1
    while (depth2 != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth2--
            XmlPullParser.START_TAG -> depth2++
        }
    }
}

