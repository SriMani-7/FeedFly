package srimani7.apps.feedfly.navigation

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.core.database.PrivateSpaceRepo
import srimani7.apps.feedfly.core.model.PrivateArticle
import srimani7.apps.feedfly.ui.articles.PrivateArticleCard
import srimani7.apps.rssparser.DateParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateSpaceScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val viewmodel = viewModel<PrivateSpaceViewmodel>()
    val groups by viewmodel.groupsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val articles by viewmodel.articlesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("Private space") },
                    navigationIcon = {
                        IconButton(onClick = navController::popBackStack) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    })
                LazyRow(
                    contentPadding = PaddingValues(10.dp, 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(groups, key = { it }) { group ->
                        FilterChip(
                            selected = viewmodel.selectedGroup == group,
                            onClick = { viewmodel.changeGroup(group) },
                            label = { Text(group) })
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 15.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(articles, key = { it.articleId }) { article ->
                PrivateArticleCard(
                    article = article,
                    pubTime = DateParser.formatDate(article.publishedTime, true) ?: "",
                    onUnLock = {
                        viewmodel.unLockArticle(it)
                    }
                )
            }
        }
    }

}

class PrivateSpaceViewmodel(application: Application) : AndroidViewModel(application) {

    private val repository = PrivateSpaceRepo(application)

    val groupsFlow = repository.groups
    var selectedGroup by mutableStateOf("Others")
        private set

    private val _articlesFlow = MutableStateFlow(emptyList<PrivateArticle>())
    val articlesFlow = _articlesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            changeGroup("Others")
        }
    }

    private var gJob: Job? = null
    fun changeGroup(group: String) {
        selectedGroup = group
        gJob?.cancel()
        gJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getPrivateArticles(group).collectLatest { articles ->
                _articlesFlow.update { articles }
            }
        }

    }

    fun unLockArticle(it: Long) {
        viewModelScope.launch {
            repository.unLockArticle(it)
        }
    }
}