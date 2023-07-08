package com.ithoughts.mynaa.tsd.rss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.ithoughts.mynaa.tsd.rss.ui.MainNavigation
import com.ithoughts.mynaa.tsd.ui.theme.TheSecretDairyTheme

class RssActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            TheSecretDairyTheme(false) {
                MainNavigation()
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}