package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ithoughts.mynaa.tsd.rss.vm.HomeViewModal

@Composable
fun MainNavigation(homeViewModal: HomeViewModal) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }, contentWindowInsets = WindowInsets.navigationBars,
    ) { paddingValues ->
        NavHost(navController, Home.HomeScreen.route, modifier = Modifier.padding(paddingValues)) {
            navigation(Home.HomeScreen.destination, Home.HomeScreen.route) {
                composable(Home.HomeScreen.destination) {
                    HomeScreen(navController, homeViewModal)
                }
                composable(Home.ArticlesScreen.destination + "/{id}", arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )) { entry ->
                    val long = entry.arguments?.getLong("id")
                    if (long != null && long > 0) RssScreen(long, navController)
                }
            }
            navigation(Favorites.FavoriteScreen.destination, Favorites.FavoriteScreen.route) {
                composable(Favorites.FavoriteScreen.destination) {
                    Text(text = "Coming soon")
                }
            }
            navigation(Settings.SettingsScreen.destination, Settings.SettingsScreen.route) {
                composable(Settings.SettingsScreen.destination) {
                    SettingsScreen(homeViewModal)
                }
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