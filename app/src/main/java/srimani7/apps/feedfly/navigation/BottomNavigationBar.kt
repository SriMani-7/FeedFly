package srimani7.apps.feedfly.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import srimani7.apps.feedfly.R

@Composable
fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    var currentScreen by rememberSaveable { mutableStateOf(Home.HomeScreen.route) }
    val items = rememberSaveable {
        listOf(
            Home.HomeScreen to R.drawable.home_fill_24px,
            Favorites.FavoriteScreen to R.drawable.favorite_fill_24,
            Settings.SettingsScreen to R.drawable.settings_fill_24px,
        )
    }
    NavigationBar(
        modifier = modifier,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.first.route == currentScreen,
                onClick = {
                    currentScreen = item.first.route
                    navController.navigate(item.first.route)
                },
                icon = { Icon(painterResource(item.second), null) },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        }
    }
}

