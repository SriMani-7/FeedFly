@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import srimani7.apps.feedfly.viewmodel.HomeViewModal

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModal: HomeViewModal = viewModel()
) {
    val allFeeds by homeViewModal.allFeedsFlow.collectAsState(null)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "FeedFly",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(InsertFeedScreen.route) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
                LazyRow(
                    contentPadding = PaddingValues(12.dp, 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    item {
                        FilterChip(selected = true, onClick = { /*TODO*/ }, label = { Text("All feeds") })
                    }
                    item {
                        FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text(text = "Latest") })
                    }
                    item {
                        FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text(text = "Groups")})
                    }
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedVisibility(visible = homeViewModal.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            allFeeds?.let {
                FeedGroupList(it) { feedId ->
                    navController.navigate(Home.ArticlesScreen.destination + "/${feedId}")
                }
            }
        }
    }
}
