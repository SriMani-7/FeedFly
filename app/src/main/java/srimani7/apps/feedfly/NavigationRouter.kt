package srimani7.apps.feedfly

import srimani7.apps.feedfly.navigation.Screen

object NavigationRouter {
    fun newFeedRoute() = Screen.InsertFeedScreen.destination
    fun groupOverviewScreen(name: String) = Screen.GroupOverviewScreen.destination + "/${name}"
    fun articlesScreenRoute(id: Long) = Screen.ArticlesScreen.destination + "/${id}"
    fun privateSpaceRoute() = Screen.PrivateSpaceScreen.destination
    fun labelRoute(id: Long) = "labels/$id"
    fun readLaterRoute() = Screen.ReadLaterScreen.destination
}