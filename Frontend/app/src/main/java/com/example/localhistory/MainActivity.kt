package com.example.localhistory

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowInsetsControllerCompat
import com.example.localhistory.ui.theme.LocalHistoryTheme
import com.example.localhistory.utils.currentLanguage
import com.example.localhistory.utils.updateLocale
import dagger.hilt.android.AndroidEntryPoint

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
            val isDark = isSystemInDarkTheme()

            LocalHistoryTheme {
                App()
            }

            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !isDark
            }
        }
    }
}