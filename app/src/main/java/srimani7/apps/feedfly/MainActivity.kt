package srimani7.apps.feedfly

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.preferences.AppTheme
import srimani7.apps.feedfly.navigation.URL_REGEX
import srimani7.apps.feedfly.viewmodel.HomeViewModal

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<HomeViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val feedUrl: String? = addFeedFromShare()

        enableEdgeToEdge()

        setContent {
            val appTheme by viewModel.settingsStateFlow.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()
            val darkTheme by remember(appTheme) {
                mutableStateOf(
                    when (appTheme.theme) {
                        AppTheme.SYSTEM_DEFAULT -> isDarkTheme
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                    }
                )
            }
            TheSecretDairyTheme(darkTheme, dynamicColor = appTheme.useDynamicTheme) {
                MainNavigation(viewModel, feedUrl)
            }
        }


    }

    private fun addFeedFromShare(): String? {
        if (intent?.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                val extra = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (extra?.matches(URL_REGEX.toRegex()) == true) {
                    return intent.getStringExtra(Intent.EXTRA_TEXT)
                } else Toast.makeText(this, "Un supported operation", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Un supported MIME type", Toast.LENGTH_SHORT).show()
        }; return null
    }
}