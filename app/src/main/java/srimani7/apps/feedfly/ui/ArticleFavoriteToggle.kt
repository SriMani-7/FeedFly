package srimani7.apps.feedfly.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.design.favorite_color

@Composable
fun ArticleFavoriteToggle(pinned: Boolean, onPinChange: (Boolean) -> Unit) {

    IconToggleButton(
        checked = pinned,
        onCheckedChange = {
            onPinChange(it)
        },
        modifier = Modifier
    ) {
        Icon(
            painterResource(if (pinned) R.drawable.favorite_fill_24 else R.drawable.favorite_outline_24),
            contentDescription = "favorite",
            modifier = Modifier.size(30.dp),
            tint = favorite_color
        )
    }
}