package srimani7.apps.feedfly.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.preferences.model.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppThemeChooser(theme: AppTheme, onToggle: (AppTheme) -> Unit) {
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