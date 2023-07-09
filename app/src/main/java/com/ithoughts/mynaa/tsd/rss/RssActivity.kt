package com.ithoughts.mynaa.tsd.rss

import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ithoughts.mynaa.tsd.rss.ui.AppTheme
import com.ithoughts.mynaa.tsd.rss.ui.MainNavigation
import com.ithoughts.mynaa.tsd.rss.vm.HomeViewModal
import com.ithoughts.mynaa.tsd.ui.theme.TheSecretDairyTheme

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RssActivity : ComponentActivity() {

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