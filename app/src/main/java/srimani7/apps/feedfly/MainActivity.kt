package srimani7.apps.feedfly

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import srimani7.apps.feedfly.navigation.URL_REGEX
import srimani7.apps.feedfly.ui.theme.TheSecretDairyTheme
import srimani7.apps.feedfly.viewmodel.HomeViewModal

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<HomeViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        var addLink: String? = null
        if (intent?.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                val extra = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (extra?.matches(URL_REGEX.toRegex()) == true) {
                    addLink = intent.getStringExtra(Intent.EXTRA_TEXT)
                } else Toast.makeText(this, "Un supported operation", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Un supported MIME type", Toast.LENGTH_SHORT).show()
        }
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
                MainNavigation(viewModel, addLink)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}