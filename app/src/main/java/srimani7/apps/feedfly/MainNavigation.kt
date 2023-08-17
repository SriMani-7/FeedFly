package srimani7.apps.feedfly

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import srimani7.apps.feedfly.navigation.ArticlesScreen
import srimani7.apps.feedfly.navigation.BottomNavigationBar
import srimani7.apps.feedfly.navigation.FavoriteScreen
import srimani7.apps.feedfly.navigation.Favorites
import srimani7.apps.feedfly.navigation.Home
import srimani7.apps.feedfly.navigation.HomeScreen
import srimani7.apps.feedfly.navigation.InsertFeedScreen
import srimani7.apps.feedfly.navigation.NewFeedScreen
import srimani7.apps.feedfly.navigation.Settings
import srimani7.apps.feedfly.navigation.SettingsScreen
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun MainNavigation(homeViewModal: HomeViewModal, addLink: String?) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    var showBottomBar by remember { mutableStateOf(true) }

    LaunchedEffect(currentRoute) {
        showBottomBar = when (currentRoute?.destination?.route) {
            Home.HomeScreen.destination,
            Favorites.FavoriteScreen.destination,
            Settings.SettingsScreen.destination -> true

            else -> false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NavHost(navController, Home.HomeScreen.route, modifier = Modifier) {
            navigation(Home.HomeScreen.destination, Home.HomeScreen.route) {
                composable(Home.HomeScreen.destination) {
                    HomeScreen(homeViewModal, navController::navigate)
                }
                composable(
                    Home.ArticlesScreen.destination + "/{id}", arguments = listOf(
                        navArgument("id") { type = NavType.LongType }
                    )) { entry ->
                    val long = entry.arguments?.getLong("id")
                    if (long != null && long > 0) ArticlesScreen(long, navController)
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

            composable(InsertFeedScreen.route) {
                NewFeedScreen(homeViewModal, addLink) { navController.popBackStack() }
            }
        }
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(navController)
        }
    }
    LaunchedEffect(addLink) {
        addLink?.let { navController.navigate(InsertFeedScreen.route) }
    }
}


@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, "back")
    }
}

object MainNavigation {
    fun newFeedRoute() = InsertFeedScreen.route
    fun articlesScreenRoute(id: Long) = Home.ArticlesScreen.destination + "/${id}"
}