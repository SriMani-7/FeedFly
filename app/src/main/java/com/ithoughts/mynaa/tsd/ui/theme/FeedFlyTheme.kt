package com.ithoughts.mynaa.tsd.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val favorite: Color,
    val onFavorite: Color,
    val favoriteContainer: Color,
    val onFavoriteContainer: Color
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        favorite = Color.Unspecified,
        onFavorite = Color.Unspecified,
        favoriteContainer = Color.Unspecified,
        onFavoriteContainer = Color.Unspecified
    )
}

object FeedFlyTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}