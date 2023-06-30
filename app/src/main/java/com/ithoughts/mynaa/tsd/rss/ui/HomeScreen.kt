@file:OptIn(ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ithoughts.mynaa.tsd.rss.FeedsViewModal
import com.ithoughts.mynaa.tsd.ui.theme.TheSecretDairyTheme

@Composable
fun HomeScreen(feedsViewModal: FeedsViewModal = viewModel()) {
    val navController = rememberNavController()
    val isInDarkTheme = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(isInDarkTheme) }

    TheSecretDairyTheme(isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController, Screens.FeedsScreen.route) {
                composable(Screens.FeedsScreen.route) {
                    FeedsScreen(feedsViewModal, navController) { isDarkTheme = it }
                }
                composable(Screens.ArticleScreen.route + "/{id}", arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )) { entry ->
                    val long = entry.arguments?.getLong("id")
                    if (long != null && long > 0) RssScreen(long, navController)
                }
            }
        }
    }
}
