package srimani7.apps.feedfly.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.R
import kotlin.math.max

@Composable
fun NumberCapsule(
    days: MutableIntState,
    modifier: Modifier = Modifier,
    suffix: String = ""
) {
    AssistChip(
        onClick = {},
        label = {
            AnimatedContent(days.intValue, label = "", transitionSpec = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) togetherWith slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down
                )
            }) {
                Text(text = "$it $suffix")
            }
        },
        leadingIcon = {
            IconButton(onClick = {
                days.intValue = max(1, days.intValue - 1)
            }) {
                Icon(
                    painterResource(R.drawable.remove_24px),
                    null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                days.intValue = minOf(30, days.intValue + 1)
            }) {
                Icon(
                    painterResource(R.drawable.add_24px), null, modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }, modifier = modifier,
        interactionSource = remember { MutableInteractionSource() },
        shape = MaterialTheme.shapes.extraLarge
    )
}