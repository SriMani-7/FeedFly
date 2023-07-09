@file:OptIn(ExperimentalMaterial3Api::class)

package com.ithoughts.mynaa.tsd.rss.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ithoughts.mynaa.tsd.rss.vm.HomeViewModal

@Composable
fun SettingsScreen(homeViewModal: HomeViewModal) {
    val selectedTheme by homeViewModal.appThemeState.collectAsState()
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Settings") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Text(
                    text = "App theme",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                AppTheme.values().forEach { appTheme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = appTheme == selectedTheme,
                                onClick = {
                                    homeViewModal.updateSettings(appTheme)
                                },
                                role = Role.RadioButton
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = appTheme.label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        RadioButton(
                            selected = (appTheme == selectedTheme),
                            onClick = null
                        )
                    }
                }
            }
        }
    }
}