package srimani7.apps.feedfly.feature.labels.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import srimani7.apps.feedfly.navigation.Screen
import srimani7.apps.feedfly.ui.articles.RssItemsColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelOverviewScaffold(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    onDeleteArticle: (Long) -> Unit
) {
    val lvm = viewModel<srimani7.apps.feedfly.viewmodel.LabelViewModel>()
    val label by lvm.labelFlow.collectAsStateWithLifecycle(initialValue = null)
    val articles by lvm.articlesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(title = { Text(label?.labelName ?: "Unknown") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            RssItemsColumn(
                dateListMap = articles,
                onDeleteArticle = onDeleteArticle,
                onLongClick = {},
                onChangeArticleLabel = { aId, lId ->
                    onNavigate(Screen.ChangeLabelDialog.destination + "/$aId?label=${lId ?: -1L}")
                }
            )
        }
    }
}