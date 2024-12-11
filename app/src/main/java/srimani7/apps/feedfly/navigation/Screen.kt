package srimani7.apps.feedfly.navigation

import androidx.annotation.DrawableRes
import srimani7.apps.feedfly.R

sealed class Screen(val destination: String) {
    data object FavoriteScreen : Screen("favorite_screen_destination")
    data object HomeScreen : Screen("home_screen_destination")
    data object ArticlesScreen : Screen("articles_screen_destination")
    data object SettingsScreen : Screen("settings_main_destination")
    data object InsertFeedScreen : Screen("insert_feed_screen")
    data object RemoveArticlesScreen: Screen("remove_articles_screen")
    data object ChangeLabelDialog: Screen("change_article_label_dialog")
    data object PrivateSpaceScreen: Screen("private_space")

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
    data object Home : NavItem("Home", R.drawable.home_fill_24px, "home_route")
    data object Favorites : NavItem("Favorites", R.drawable.favorite_fill_24, "favorites_route")
    data object Settings : NavItem("Settings", R.drawable.settings_fill_24px, "settings_route")
}