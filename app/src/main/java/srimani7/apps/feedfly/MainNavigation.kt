package srimani7.apps.feedfly

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import srimani7.apps.feedfly.navigation.AddUrlDialog
import srimani7.apps.feedfly.navigation.BottomNavigationBar
import srimani7.apps.feedfly.navigation.FavoriteScreen
import srimani7.apps.feedfly.navigation.Favorites
import srimani7.apps.feedfly.navigation.Home
import srimani7.apps.feedfly.navigation.HomeScreen
import srimani7.apps.feedfly.navigation.InsertFeedScreen
import srimani7.apps.feedfly.navigation.RssScreen
import srimani7.apps.feedfly.navigation.Settings
import srimani7.apps.feedfly.navigation.SettingsScreen
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun MainNavigation(homeViewModal: HomeViewModal) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            when (currentRoute?.destination?.route) {
                Home.HomeScreen.destination, Favorites.FavoriteScreen.destination, Settings.SettingsScreen.destination -> {
                    BottomNavigationBar(navController)
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { paddingValues ->
        NavHost(navController, Home.HomeScreen.route, modifier = Modifier.padding(paddingValues)) {
            navigation(Home.HomeScreen.destination, Home.HomeScreen.route) {
                composable(Home.HomeScreen.destination) {
                    HomeScreen(navController, homeViewModal)
                }
                composable(
                    Home.ArticlesScreen.destination + "/{id}", arguments = listOf(
                        navArgument("id") { type = NavType.LongType }
                    )) { entry ->
                    val long = entry.arguments?.getLong("id")
                    if (long != null && long > 0) RssScreen(long, navController)
                }
            }
            navigation(Favorites.FavoriteScreen.destination, Favorites.FavoriteScreen.route) {
                composable(Favorites.FavoriteScreen.destination) {
                    FavoriteScreen(homeViewModal)
                }
            }
            navigation(Settings.SettingsScreen.destination, Settings.SettingsScreen.route) {
                composable(Settings.SettingsScreen.destination) {
                    SettingsScreen(homeViewModal)
                }
            }

            dialog(InsertFeedScreen.route) {
                AddUrlDialog(homeViewModal) { navController.popBackStack() }
            }
        }
    }
}


@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, "back")
    }
}