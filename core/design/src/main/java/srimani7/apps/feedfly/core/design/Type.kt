package srimani7.apps.feedfly.core.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

// open sans font family
val openSans = FontFamily(
    Font(R.font.opensans_bold, FontWeight.Bold),
    Font(R.font.opensans_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.opensans_extra_bold, FontWeight.ExtraBold),
    Font(R.font.opensans_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.opensans_italic, style = FontStyle.Italic),
    Font(R.font.opensans_light, FontWeight.Light),
    Font(R.font.opensans_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.opensans_medium, FontWeight.Medium),
    Font(R.font.opensans_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.opensans_regular),
    Font(R.font.opensans_semi_bold, FontWeight.SemiBold),
    Font(R.font.opensans_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
)

val displayFontFamily = openSans
val bodyFontFamily = openSans

// Default Material 3 typography values
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)