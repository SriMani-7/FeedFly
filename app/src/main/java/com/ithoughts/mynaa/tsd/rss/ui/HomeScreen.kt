@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.ColumnInfo
import androidx.room.Relation
import com.ithoughts.mynaa.tsd.rss.db.Feed
import com.ithoughts.mynaa.tsd.rss.vm.HomeViewModal

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModal: HomeViewModal = viewModel()
) {
    val groups by homeViewModal.groupsFlow.collectAsState(null)
    val otherFeeds by homeViewModal.otherFeeds.collectAsState(null)
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FeedFly") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedVisibility(visible = homeViewModal.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(
                contentPadding = PaddingValues(top = 12.dp, bottom = 36.dp)
            ) {
                groups?.let { groups ->
                    items(groups, key = { it.name ?: "" }) { feedGroup ->
                        feedGroup.name?.let {
                            FeedGroupList(feedGroup.name, feedGroup.feeds) { feedId ->
                                navController.navigate(Home.ArticlesScreen.destination + "/${feedId}")
                            }
                        }
                    }
                    otherFeeds?.let {
                        item(key = "Others") {
                            FeedGroupList("Other feeds", it) { feedId ->
                                navController.navigate(Home.ArticlesScreen.destination + "/${feedId}")
                            }
                        }
                    }
                }
            }
        }
        if (showDialog) {
            AddUrlDialog(
                onDismiss = { showDialog = false },
                onSubmit = {
                    showDialog = false
                    homeViewModal.insertFeed(it)
                }
            )
        }
    }
}

data class FeedGroup(
    @ColumnInfo("name")
    val name: String?,
    @Relation(
        entityColumn = "group_name",
        parentColumn = "name"
    ) val feeds: List<Feed>
)