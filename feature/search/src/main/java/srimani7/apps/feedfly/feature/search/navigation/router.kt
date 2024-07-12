package srimani7.apps.feedfly.feature.search.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import srimani7.apps.feedfly.feature.search.viewmodel.FeedInsertViewModel

fun NavGraphBuilder.newFeedScreen(navController: NavController) {
    composable("insert?search={query}") { entry ->
        val param = entry.arguments?.getString("query")
        val feedInsertViewModel = hiltViewModel<FeedInsertViewModel>()
        NewFeedScreen(feedInsertViewModel, param, navController::popBackStack)
    }
}

fun NavController.navigateSearchScreen(query: String? = null) = navigate("insert?search=$query")