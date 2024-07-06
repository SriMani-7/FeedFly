@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.data.AppTheme
import srimani7.apps.feedfly.data.UserSettingsRepo
import srimani7.apps.feedfly.viewmodel.SettingsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settingsFlow.collectAsStateWithLifecycle(
        initialValue = UserSettingsRepo.Settings(
            AppTheme.SYSTEM_DEFAULT,
            "",
            false
        )
    )
    val articlePreferences by viewModel.articlePreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserSettingsRepo.ArticlePreference()
    )

    Scaffold(
        topBar = {
            ThemeSegmentedButton(settings.theme, viewModel::updateSettings)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    SwitchPreference(
                        text = "Use wallpaper colors theming",
                        checked = settings.useDynamicTheme,
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

@Composable
fun SwitchPreference(text: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    TextButton(onClick = { onChange(!checked) }) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSegmentedButton(theme: AppTheme, onToggle: (AppTheme) -> Unit) {
    MediumTopAppBar(
        title = { Text(text = "Settings") },
        actions = {
            Row(
                modifier = Modifier
                    .selectableGroup()
                    .background(Color.Transparent, RoundedCornerShape(14.dp))
                    .border(
                        IconButtonDefaults.outlinedIconButtonBorder(true),
                        IconButtonDefaults.outlinedShape
                    )
                    .padding(6.dp, 4.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf(
                    AppTheme.SYSTEM_DEFAULT to R.drawable.theme_auto_24px,
                    AppTheme.DARK to R.drawable.theme_dark_mode_24px,
                    AppTheme.LIGHT to R.drawable.theme_light_mode_24px
                ).forEach { themeIntPair ->
                    OutlinedIconToggleButton(
                        checked = theme == themeIntPair.first,
                        onCheckedChange = { onToggle(themeIntPair.first) },
                        border = null,
                        modifier = Modifier.size(38.dp),
                    ) {
                        Icon(
                            painterResource(themeIntPair.second),
                            themeIntPair.first.toString(),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    )
}