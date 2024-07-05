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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import srimani7.apps.feedfly.core.database.PrivateSpaceRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateSpaceScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val viewmodel = viewModel<PrivateSpaceViewmodel>()
    val groups by viewmodel.groupsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

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
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

        }
    }

}

class PrivateSpaceViewmodel(application: Application) : AndroidViewModel(application) {

    private val repository = PrivateSpaceRepo(application)

    val groupsFlow = repository.groups
    var selectedGroup by mutableStateOf("Others")
        private set

    fun changeGroup(group: String) {
        selectedGroup = group
    }
}