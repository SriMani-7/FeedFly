@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ithoughts.mynaa.tsd.rss.vm.HomeViewModal

@Composable
fun HomeScreen(
    themeState: MutableState<Boolean>,
    navController: NavController,
    homeViewModal: HomeViewModal = viewModel()
) {
    val groups by homeViewModal.groupsFlow.collectAsState(null)
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("FeedFly") },
                actions = { ThemeToggle(themeState) }
            )
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
            groups?.let { it1 ->
                FeedGroups(feedGroupList = it1) { groupName ->
                    val path = groupName?.let { "?groupName=" + Uri.encode(groupName) } ?: ""
                    navController.navigate(Screens.FeedsScreen.route + path)
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

@Composable
fun FeedGroups(feedGroupList: List<FeedGroup>, onClick: (String?) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxSize(),
        verticalItemSpacing = 8.dp
    ) {
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            Text(text = "Groups", fontWeight = FontWeight.Normal, fontSize = 14.sp)
        }
        items(feedGroupList, key = { it.name ?: "" }) {
            ElevatedAssistChip(
                onClick = { onClick(it.name) },
                label = {
                    Text(
                        text = it.name ?: "The rest",
                    )
                },
                trailingIcon = { Text(it.count.toString()) },
                modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

data class FeedGroup(val name: String?, val count: Int)