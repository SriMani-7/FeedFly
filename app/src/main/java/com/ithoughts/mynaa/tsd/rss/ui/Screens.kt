package com.ithoughts.mynaa.tsd.rss.ui

sealed class Screens(val route: String) {
    object FeedsScreen : Screens("feeds_screen_route")
    object ArticleScreen : Screens("articles_screen")
}