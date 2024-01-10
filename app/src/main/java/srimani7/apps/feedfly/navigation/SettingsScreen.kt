@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.data.AppTheme

@Composable
fun SettingsScreen(
    selectedTheme: AppTheme,
    updateSettings: (AppTheme) -> Unit
) {
    Scaffold(
        topBar = {
            ThemeSegmentedButton(selectedTheme, updateSettings)
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}

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

@Preview(wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Composable
fun ThemeSettingPreview() {
    var theme by remember {
        mutableStateOf(AppTheme.SYSTEM_DEFAULT)
    }
    SettingsScreen(theme) { theme = it }
}