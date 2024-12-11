package srimani7.apps.feedfly.core.data

import srimani7.apps.feedfly.core.database.entity.ArticleItem
import srimani7.apps.feedfly.core.database.entity.Feed
import srimani7.apps.feedfly.core.database.entity.FeedImage
import srimani7.apps.feedfly.core.model.FeedModel
import srimani7.apps.rssparser.DateParser
import srimani7.apps.rssparser.elements.Channel
import srimani7.apps.rssparser.elements.ChannelImage
import srimani7.apps.rssparser.elements.ChannelItem
import java.util.Date

internal fun ChannelItem.asArticleItem(feedId: Long) = ArticleItem(
    title = title ?: "",
    link = link ?: "",
    category = categories.joinToString(separator = ", "),
    lastFetched = Date(),
    pubDate = DateParser.parseDate(pubDate),
    description = description,
    author = author,
    feedId = feedId
)

internal fun ChannelImage.asFeedImage(feedId: Long) = FeedImage(
    link = link ?: "",
    title = title ?: "",
    url = url ?: "",
    feedId = feedId,
    description = description,
    height = height ?: FeedImage.DEFAULT_HEIGHT,
    width = width ?: FeedImage.DEFAULT_WIDTH
)

internal fun FeedModel.asFeed(channel: Channel) = Feed(
    description = channel.description,
    link = channel.link ?: "",
    title = channel.title ?: "",
    lastBuildDate = channel.lastBuildDate ?: Date(),
    language = channel.language,
    managingEditor = channel.managingEditor,
    copyright = channel.copyright,
    id = id,
    group = groupName,
    feedUrl = feedUrl
)

internal fun Channel.asFeed(group: String) = Feed(
    feedUrl = feedUrl,
    description = description,
    link = link ?: "",
    title = title ?: "",
    lastBuildDate = null,
    group = group,
    language = language,
    managingEditor = managingEditor,
    copyright = copyright
)