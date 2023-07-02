package com.ithoughts.mynaa.tsd.rss.ui

sealed class Screens(val route: String) {
    object HomeScreen: Screens("home_screen_route")
    object FeedsScreen : Screens("feeds_screen_route")
    object ArticleScreen : Screens("articles_screen")
}