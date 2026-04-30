package com.example.localhistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import com.example.localhistory.ui.theme.LocalHistoryTheme
import com.example.localhistory.utils.currentLanguage
import com.example.localhistory.utils.updateLocale
import dagger.hilt.android.AndroidEntryPoint
import com.example.localhistory.ui.theme.AppThemeState
import com.example.localhistory.ui.theme.LocalAppThemeState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply locale BEFORE setContent so all resources load correctly
        updateLocale(this, currentLanguage).also {
            resources.updateConfiguration(it.resources.configuration, it.resources.displayMetrics)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDark by rememberSaveable { mutableStateOf(false) }

            CompositionLocalProvider(
                LocalAppThemeState provides AppThemeState(
                    isDarkTheme = isDark,
                    setDarkTheme = { isDark = it }
                )
            ) {
                LocalHistoryTheme(darkTheme = isDark) {
                    App()

                    SideEffect {
                        window.statusBarColor = Color.Transparent.toArgb()

                        WindowInsetsControllerCompat(window, window.decorView)
                            .isAppearanceLightStatusBars = !isDark
                    }
                }
            }
        }
    }
}