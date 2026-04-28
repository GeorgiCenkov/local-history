package com.example.localhistory.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary          = Brand300,
    onPrimary        = White,
    secondary        = Accent,
    onSecondary      = White,
    background       = Gray100,
    onBackground     = Gray900,
    surface          = White,
    onSurface        = Gray900,
    surfaceVariant   = Gray200,
    onSurfaceVariant = Gray800,
)

private val DarkColors = darkColorScheme(
    primary          = Accent,
    onPrimary        = White,
    secondary        = Brand300,
    onSecondary      = White,
    background       = Gray900,
    onBackground     = Gray100,
    surface          = Gray800,
    onSurface        = Gray100,
    surfaceVariant   = Brand200,
    onSurfaceVariant = Gray200,
)

// Global toggle state — lives at app level
var isDarkTheme by mutableStateOf(false)

@Composable
fun LocalHistoryTheme(
    darkTheme: Boolean = isDarkTheme,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // Dynamic color — Android 12+ only
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkTheme ->
            dynamicLightColorScheme(context)

        // Fallback for Android 11 and below — your custom colors
        darkTheme  -> DarkColors
        else       -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content
    )
}