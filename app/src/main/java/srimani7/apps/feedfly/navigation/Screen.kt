package srimani7.apps.feedfly.navigation

sealed class Screen(
    val destination: String,
    val route: String
)

sealed class Home(destination: String): Screen(destination,"home_nav_route") {
    object HomeScreen: Home("home_screen_destination")
    object ArticlesScreen: Home("articles_screen_destination")
}

sealed class Favorites(destination: String): Screen(destination, "favorites_nav_route") {
    object FavoriteScreen: Favorites("favorite_screen_destination")
}
sealed class Settings(destination: String): Screen(destination,"settings_nav_route") {
    object SettingsScreen: Settings("settings_main_destination")
}

object InsertFeedScreen: Screen("insert_feed_screen", "insert_feed_route")