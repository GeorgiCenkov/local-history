package com.example.localhistory.ui.theme

import androidx.compose.runtime.compositionLocalOf

data class AppThemeState(
    val isDarkTheme: Boolean,
    val setDarkTheme: (Boolean) -> Unit
)

val LocalAppThemeState = compositionLocalOf<AppThemeState> {
    error("LocalAppThemeState not provided")
}