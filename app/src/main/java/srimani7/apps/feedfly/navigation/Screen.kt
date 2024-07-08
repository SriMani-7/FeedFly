package srimani7.apps.feedfly.navigation

import androidx.annotation.DrawableRes
import srimani7.apps.feedfly.R

sealed class Screen(val destination: String) {
    object HomeScreen : Screen("home_screen_destination")
    object ArticlesScreen : Screen("articles_screen_destination")
    object SettingsScreen : Screen("settings_main_destination")
    object InsertFeedScreen : Screen("insert_feed_screen")
    object RemoveArticlesScreen: Screen("remove_articles_screen")
    object ChangeLabelDialog: Screen("change_article_label_dialog")
    data object PrivateSpaceScreen: Screen("private_space")
    data object GroupOverviewScreen: Screen("home_screen_group")
}

sealed class NavItem(
    val label: String,
    @DrawableRes val iconRes: Int,
    val navRoute: String
) {
    object Home : NavItem("Home", R.drawable.home_fill_24px, "home_route")
    object Settings : NavItem("Settings", R.drawable.settings_fill_24px, "settings_route")
}