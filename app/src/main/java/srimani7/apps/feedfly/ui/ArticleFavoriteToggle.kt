package srimani7.apps.feedfly.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.ui.theme.favorite_color

@Composable
fun ArticleFavoriteToggle(pinned: Boolean, onPinChange: (Boolean) -> Unit) {
    IconToggleButton(pinned, onCheckedChange = {
        onPinChange(it)
    }, modifier = Modifier) {
        Crossfade(
            targetState = pinned,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) {
            val resId = if (it) R.drawable.favorite_fill_24
            else R.drawable.favorite_outline_24
            Icon(
                painterResource(resId), "favorite",
                modifier = Modifier.size(30.dp),
                tint = favorite_color
            )
        }
    }
}