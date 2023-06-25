package com.ithoughts.mynaa.tsd.rss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.ithoughts.mynaa.tsd.rss.ui.HomeScreen
import com.ithoughts.mynaa.tsd.ui.theme.TheSecretDairyTheme

class RssActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheSecretDairyTheme(false) {
                HomeScreen()
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}