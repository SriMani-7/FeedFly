@file:OptIn(ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ithoughts.mynaa.tsd.rss.FeedsViewModal

@Composable
fun HomeScreen(feedsViewModal: FeedsViewModal = viewModel()) {
    val navController = rememberNavController()
    Surface {
        NavHost(navController, Screens.FeedsScreen.route) {
            composable(Screens.FeedsScreen.route) {
                FeedsScreen(feedsViewModal, navController)
            }
            composable(Screens.ArticleScreen.route + "/{id}", arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )) { entry ->
                val long = entry.arguments?.getLong("id")
                if (long != null && long > 0) RssScreen(long)
            }
        }
    }
}
