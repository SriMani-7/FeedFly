package srimani7.apps.feedfly.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navItems: List<NavItem> = listOf(NavItem.Home, NavItem.Favorites, NavItem.Settings)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar by remember(currentDestination?.route) {
        derivedStateOf {
            Screen.showBottomBar(currentDestination?.route)
        }
    }
    AnimatedVisibility(showBottomBar, modifier = modifier, exit = fadeOut()) {
        NavigationBar {
            navItems.forEach { navItem ->
                BottomNavItem(selected = currentDestination?.hierarchy?.any { it.route == navItem.navRoute } == true,
                    navItem = navItem) {
                    navController.navigate(navItem.navRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItem(selected: Boolean, navItem: NavItem, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(painterResource(navItem.iconRes), null) },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.6f),
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        label = { Text(navItem.label) },
    )
}