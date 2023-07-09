package srimani7.apps.feedfly.database

import srimani7.apps.feedfly.database.entity.ArticleItem
import srimani7.apps.feedfly.database.entity.Feed

class FeedArticle(val feed: Feed, val articles: List<ArticleItem>)