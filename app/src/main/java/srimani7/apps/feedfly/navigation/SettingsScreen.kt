@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import srimani7.apps.feedfly.core.preferences.model.ArticlePreference
import srimani7.apps.feedfly.core.preferences.model.ThemePreference
import srimani7.apps.feedfly.ui.settings.AppThemeChooser
import srimani7.apps.feedfly.ui.settings.SwitchPreference
import srimani7.apps.feedfly.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val themePreference by viewModel.themePreferenceFlow.collectAsStateWithLifecycle(
        initialValue = ThemePreference(AppTheme.SYSTEM_DEFAULT)
    )
    val articlePreferences by viewModel.articlePreferencesFlow.collectAsStateWithLifecycle(
        initialValue = ArticlePreference()
    )

    Scaffold(
        topBar = {
            MediumTopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }},
                title = { Text("Settings") }, actions = {
                AppThemeChooser(themePreference.theme, viewModel::updateSettings)
            })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    SwitchPreference(
                        text = "Use wallpaper colors theming",
                        checked = themePreference.useDynamicTheme,
                        onChange = viewModel::useDynamicTheme
                    )
                }
            }

            item {
                SwitchPreference(
                    text = "Swipe to delete article",
                    checked = articlePreferences.swipeToDelete,
                    viewModel::setArticleSwipe
                )
            }
            item {
                SwitchPreference(
                    text = "Long click to private",
                    checked = articlePreferences.longClickToPrivate,
                    viewModel::setArticleLongClick
                )
            }
        }
    }
}

