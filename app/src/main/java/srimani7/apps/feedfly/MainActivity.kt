package srimani7.apps.feedfly

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.preferences.UserSettingsRepo
import srimani7.apps.feedfly.core.preferences.model.AppTheme
import srimani7.apps.feedfly.core.preferences.model.ThemePreference
import srimani7.apps.feedfly.navigation.MainNavHost
import srimani7.apps.feedfly.navigation.URL_REGEX

class MainActivity : ComponentActivity() {

    private val userSettingsRepo by lazy { UserSettingsRepo(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val feedUrl: String? = addFeedFromShare()

        enableEdgeToEdge()

        setContent {
            val themePreference by userSettingsRepo.themePreferenceFlow.collectAsStateWithLifecycle(
                initialValue = ThemePreference(AppTheme.SYSTEM_DEFAULT)
            )
            val isDarkTheme = isSystemInDarkTheme()
            val darkTheme by remember(themePreference) {
                mutableStateOf(
                    when (themePreference.theme) {
                        AppTheme.SYSTEM_DEFAULT -> isDarkTheme
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                    }
                )
            }
            TheSecretDairyTheme(darkTheme, dynamicColor = themePreference.useDynamicTheme) {
                MainNavHost(feedUrl)
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