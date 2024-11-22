package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import srimani7.apps.feedfly.NavigationRouter
import srimani7.apps.feedfly.feature.search.navigation.navigateSearchScreen
import srimani7.apps.feedfly.feature.search.navigation.newFeedScreen
import srimani7.apps.feedfly.viewmodel.HomeViewModal
import srimani7.apps.feedfly.viewmodel.SettingsViewModel

@Composable
fun MainNavHost(addLink: String?) {
    val homeViewModal = viewModel<HomeViewModal>()
    val navController = rememberNavController()
    val deletingState by homeViewModal.deletingStateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(navController, Screen.HomeScreen.destination, modifier = Modifier) {
            composable(Screen.HomeScreen.destination) {
                HomeScreen(homeViewModal, navController::navigate)
            }
            composable(
                Screen.ArticlesScreen.destination + "/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )) {
                ArticlesScreen(navController)
            }
            composable(Screen.LabelsScreen.destination) {
                val labels by homeViewModal.labels.collectAsStateWithLifecycle(initialValue = emptyList())
                LabelsScaffold(
                    labelData = labels,
                    onClick = { id, _ -> navController.navigate(NavigationRouter.labelRoute(id)) },
                    onBackClick = navController::popBackStack,
                    onAddNewLabel = {
                        homeViewModal.addLabel(it)
                    })
            }
            composable("labels/{id}", arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )) {
                LabelOverviewScaffold(
                    onBack = navController::popBackStack,
                    onNavigate = navController::navigate,
                    onDeleteArticle = homeViewModal::removeArticle
                )
            }
            composable(Screen.GroupOverviewScreen.destination + "/{group}") {
                GroupOverviewScreen(navController)
            }
            composable(Screen.SettingsScreen.destination) {
                val viewmodel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(viewmodel, navController)
            }

            newFeedScreen(navController)

            composable(Screen.PrivateSpaceScreen.destination) {
                PrivateSpaceScreen(navController = navController)
            }
            dialog(
                route = Screen.RemoveArticlesScreen.destination + "/{feedId}",
                arguments = listOf(
                    navArgument("feedId") { type = NavType.LongType }
                )
            ) {
                val feedId = it.arguments?.getLong("feedId")
                RemoveArticlesScreen(navController::popBackStack) { days ->
                    homeViewModal.deleteOldArticles(feedId, days)
                    navController.popBackStack()
                }
            }

            dialog(
                route = Screen.ChangeLabelDialog.destination + "/{articleId}?label={label}",
                arguments = listOf(
                    navArgument("articleId") { type = NavType.LongType },
                    navArgument("label") { type = NavType.LongType }
                )
            ) {
                val articleId = it.arguments?.getLong("articleId")

                val labels by homeViewModal.labels.collectAsStateWithLifecycle(initialValue = emptyList())

                if (articleId != null) {
                    ChangeArticleLabelDialog(
                        pLabelId = it.arguments?.getLong("label"),
                        labels = labels,
                        cancel = navController::popBackStack,
                        update = { labelId ->
                            if (labelId == -1L) homeViewModal.removeArticleLabel(articleId)
                            else homeViewModal.updateArticleLabel(articleId, labelId)
                            navController.popBackStack()
                        })
                }
            }

            composable(
                route = Screen.ReadLaterScreen.destination,
                deepLinks = listOf(navDeepLink { uriPattern = "feedfly://read_later" })
            ) {
                ReadLaterScreen(
                    onBack = navController::popBackStack
                )
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
        }
    }
    LaunchedEffect(addLink) {
        addLink?.let { navController.navigateSearchScreen(it) }
    }
}