@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import srimani7.apps.feedfly.core.preferences.model.ArticlePreference
import srimani7.apps.feedfly.core.preferences.model.ThemePreference
import srimani7.apps.feedfly.ui.settings.AppThemeChooser
import srimani7.apps.feedfly.ui.settings.SwitchPreference
import srimani7.apps.feedfly.viewmodel.SettingsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

    val readLaterTime by viewModel.readLaterTimeFlow.collectAsStateWithLifecycle(null)

    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
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

            item {
                TextButton({
                    showDialog = true
                }) {
                    Column {
                        Text("Read Later notification")
                        Text(readLaterTime?.let {
                            LocalTime.of(it.hour, it.minute)
                                .format(DateTimeFormatter.ofPattern("hh:mm a"))
                        }.toString())
                    }
                }
            }
        }
    }

    val timePickerState = rememberTimePickerState(is24Hour = false)

    if (showDialog) {
        BasicAlertDialog({
            showDialog = false
        }, properties = DialogProperties()) {
            Surface {
                Column {
                    Text("Select time")
                    TimePicker(timePickerState)
                    TextButton({
                        viewModel.updateReadLaterTime(timePickerState.hour, timePickerState.minute)
                        showDialog = false
                    }) {
                        Text("Done")
                    }
                    TextButton({
                        showDialog = false
                        viewModel.updateReadLaterTime(null, null)
                    }) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}

