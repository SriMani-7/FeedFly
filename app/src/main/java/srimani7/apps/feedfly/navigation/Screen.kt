package srimani7.apps.feedfly.navigation

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
