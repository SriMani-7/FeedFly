package srimani7.apps.feedfly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import srimani7.apps.feedfly.navigation.ArticlesScreen
import srimani7.apps.feedfly.navigation.BottomNavigationBar
import srimani7.apps.feedfly.navigation.FavoriteScreen
import srimani7.apps.feedfly.navigation.HomeScreen
import srimani7.apps.feedfly.navigation.NavItem
import srimani7.apps.feedfly.navigation.NewFeedScreen
import srimani7.apps.feedfly.navigation.RemoveArticlesScreen
import srimani7.apps.feedfly.navigation.Screen
import srimani7.apps.feedfly.navigation.SettingsScreen
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun MainNavigation(homeViewModal: HomeViewModal, addLink: String?) {
    val navController = rememberNavController()
    val settings by homeViewModal.settingsStateFlow.collectAsStateWithLifecycle()
    val deletingState by homeViewModal.deletingStateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(navController, NavItem.Home.navRoute, modifier = Modifier) {
            homeNavigation(navController, homeViewModal)
            navigation(Screen.FavoriteScreen.destination, NavItem.Favorites.navRoute) {
                composable(Screen.FavoriteScreen.destination) {
                    FavoriteScreen(homeViewModal)
                }
            }
            navigation(Screen.SettingsScreen.destination, NavItem.Settings.navRoute) {
                composable(Screen.SettingsScreen.destination) {
                    SettingsScreen(settings.theme, homeViewModal::updateSettings)
                }
            }

            composable(Screen.InsertFeedScreen.destination) {
                NewFeedScreen(homeViewModal, addLink) { navController.popBackStack() }
            }

            dialog(
                route = Screen.RemoveArticlesScreen.destination + "/{feedId}",
                arguments = listOf(
                    navArgument("feedId") { type = NavType.LongType }
                )
            ) {
                val feedId = it.arguments?.getLong("feedId")
                RemoveArticlesScreen(navController::popBackStack) {
                    homeViewModal.deleteOldArticles(feedId, it)
                    navController.popBackStack()
                }
            }
        }
        Column(Modifier.align(Alignment.BottomCenter)) {
            if (deletingState) {
                Text(
                    text = "Articles removing",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 12.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            BottomNavigationBar(navController)
        }
    }
    LaunchedEffect(addLink) {
        addLink?.let { navController.navigate(Screen.InsertFeedScreen.destination) }
    }
}


@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, "back")
    }
}

object MainNavigation {
    fun newFeedRoute() = Screen.InsertFeedScreen.destination
    fun articlesScreenRoute(id: Long) = Screen.ArticlesScreen.destination + "/${id}"
}

fun NavGraphBuilder.homeNavigation(navController: NavHostController, homeViewModal: HomeViewModal) {
    navigation(Screen.HomeScreen.destination, NavItem.Home.navRoute) {
        composable(Screen.HomeScreen.destination) {
            HomeScreen(homeViewModal, navController::navigate)
        }
        composable(
            Screen.ArticlesScreen.destination + "/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )) { entry ->
            val long = entry.arguments?.getLong("id")
            if (long != null && long > 0) ArticlesScreen(long, navController)
        }
    }
}