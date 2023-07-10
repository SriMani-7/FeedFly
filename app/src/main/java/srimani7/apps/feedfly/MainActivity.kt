package srimani7.apps.feedfly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import srimani7.apps.feedfly.data.AppTheme
import srimani7.apps.feedfly.ui.theme.TheSecretDairyTheme
import srimani7.apps.feedfly.viewmodel.HomeViewModal

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<HomeViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val appTheme by viewModel.appThemeState.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()
            val darkTheme by remember(appTheme) {
                mutableStateOf(
                    when (appTheme) {
                        AppTheme.SYSTEM_DEFAULT -> isDarkTheme
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                    }
                )
            }
            TheSecretDairyTheme(darkTheme) {
                MainNavigation(viewModel)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}