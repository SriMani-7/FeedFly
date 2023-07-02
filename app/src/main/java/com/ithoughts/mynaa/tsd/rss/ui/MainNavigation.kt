@file:OptIn(ExperimentalAnimationApi::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ithoughts.mynaa.tsd.R
import com.ithoughts.mynaa.tsd.ui.theme.TheSecretDairyTheme

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val isInDarkTheme = isSystemInDarkTheme()
    val isDarkTheme = remember { mutableStateOf(isInDarkTheme) }

    TheSecretDairyTheme(isDarkTheme.value) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController, Screens.HomeScreen.route) {
                composable(Screens.HomeScreen.route) {
                    HomeScreen(isDarkTheme, navController)
                }
                composable(Screens.FeedsScreen.route + "/{groupName}", arguments = listOf(
                    navArgument("groupName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )) { entry ->
                    val groupName = entry.arguments?.getString("groupName")
                    FeedsScreen(groupName, navController)
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

@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, "back")
    }
}

@Composable
fun ThemeToggle(themeState: MutableState<Boolean>) {
    IconToggleButton(checked = themeState.value, onCheckedChange = {
        themeState.value = it
    }) {
        AnimatedContent(themeState.value) {
            if (it) Icon(painterResource(R.drawable.baseline_wb_sunny_24), "theme")
            else Icon(painterResource(R.drawable.baseline_nightlight_round_24), "theme")
        }
    }
}