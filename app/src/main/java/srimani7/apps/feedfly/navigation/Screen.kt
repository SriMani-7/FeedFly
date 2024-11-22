package srimani7.apps.feedfly.navigation

sealed class Screen(val destination: String) {
    data object HomeScreen : Screen("home_screen_destination")
    data object ArticlesScreen : Screen("articles_screen_destination")
    data object SettingsScreen : Screen("settings_main_destination")
    data object InsertFeedScreen : Screen("insert")
    data object RemoveArticlesScreen: Screen("remove_articles_screen")
    data object ChangeLabelDialog: Screen("change_article_label_dialog")
    data object PrivateSpaceScreen: Screen("private_space")
    data object GroupOverviewScreen: Screen("home_screen_group")
    data object LabelsScreen: Screen("labels")
    data object ReadLaterScreen:Screen("read_later")
}
