package srimani7.apps.feedfly.navigation

import androidx.annotation.DrawableRes
import srimani7.apps.feedfly.R

sealed class Screen(val destination: String) {
    object FavoriteScreen : Screen("favorite_screen_destination")
    object HomeScreen : Screen("home_screen_destination")
    object ArticlesScreen : Screen("articles_screen_destination")
    object SettingsScreen : Screen("settings_main_destination")
    object InsertFeedScreen : Screen("insert_feed_screen")
    object RemoveArticlesScreen: Screen("remove_articles_screen")

    companion object {
        fun showBottomBar(route: String?) = when(route) {
            HomeScreen.destination, FavoriteScreen.destination, SettingsScreen.destination -> true
            else -> false
        }
    }
}

sealed class NavItem(
    val label: String,
    @DrawableRes val iconRes: Int,
    val navRoute: String
) {
    object Home : NavItem("Home", R.drawable.home_fill_24px, "home_route")
    object Favorites : NavItem("Favorites", R.drawable.favorite_fill_24, "favorites_route")
    object Settings : NavItem("Settings", R.drawable.settings_fill_24px, "settings_route")
}