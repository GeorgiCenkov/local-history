package com.example.localhistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowInsetsControllerCompat
import com.example.localhistory.data.datastore.AuthDataStore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark = isSystemInDarkTheme()

            setContent {
                val authDataStore = AuthDataStore(applicationContext)

                App(authDataStore = authDataStore)
            }

            // Runs every time isDarkTheme changes to update the status bar icons
            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !isDark
            }

        }
    }
}