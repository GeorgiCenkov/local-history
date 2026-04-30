package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localhistory.ui.theme.LocalAppThemeState

@Composable
fun ThemeToggle() {
    val themeState = LocalAppThemeState.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (themeState.isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = themeState.isDarkTheme,
            onCheckedChange =  themeState.setDarkTheme
        )
    }
}