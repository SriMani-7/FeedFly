package srimani7.apps.feedfly

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.database.LabelRepository
import srimani7.apps.feedfly.feature.labels.ui.LabelsScaffold
import srimani7.apps.feedfly.navigation.ArticlesScreen
import srimani7.apps.feedfly.navigation.BottomNavigationBar
import srimani7.apps.feedfly.navigation.ChangeArticleLabelDialog
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
    val labelViewModel = viewModel<LabelViewModel>()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(navController, NavItem.Home.navRoute, modifier = Modifier) {
            homeNavigation(navController, homeViewModal)
            navigation(Screen.FavoriteScreen.destination, NavItem.Favorites.navRoute) {
                composable(Screen.FavoriteScreen.destination) {
                    val labels by labelViewModel.labels.collectAsStateWithLifecycle(initialValue = emptyList())
                    LabelsScaffold(
                        labelData = labels,
                        onClick = { _, _ -> },
                        onAddNewLabel = {
                        labelViewModel.addLabel(it)
                    })
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

            composable(Screen.PrivateSpaceScreen.destination) {
                // TODO: implement private space screen
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

            dialog(
                route = Screen.ChangeLabelDialog.destination + "/{articleId}?label={label}",
                arguments = listOf(
                    navArgument("articleId") { type = NavType.LongType },
                    navArgument("label") { type = NavType.LongType }
                )
            ) {
                val articleId = it.arguments?.getLong("articleId")

                val labels by labelViewModel.labels.collectAsStateWithLifecycle(initialValue = emptyList())

                if (articleId != null) {
                    ChangeArticleLabelDialog(
                        pLabelId = it.arguments?.getLong("label"),
                        labels = labels,
                        cancel = navController::popBackStack,
                        update = { labelId ->
                            if (labelId == -1L) labelViewModel.removeArticleLabel(articleId)
                            else labelViewModel.updateArticleLabel(articleId, labelId)
                            navController.popBackStack()
                        })
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
    fun privateSpaceRoute() =  Screen.PrivateSpaceScreen.destination
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

class LabelViewModel(application: Application) : AndroidViewModel(application) {
    val labelRepository = LabelRepository(application)

    val labels by lazy { labelRepository.getAllLabels() }

    fun updateArticleLabel(articleId: Long, labelId: Long) {
        viewModelScope.launch { labelRepository.updateArticleLabel(articleId, labelId) }
    }

    fun removeArticleLabel(articleId: Long) {
        viewModelScope.launch { labelRepository.removeArticleLabel(articleId) }
    }

    fun addLabel(it: String) {
        viewModelScope.launch { labelRepository.addLabel(it) }
    }
}