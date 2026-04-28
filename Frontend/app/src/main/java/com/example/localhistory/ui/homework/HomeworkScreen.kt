package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.localhistory.ui.theme.isDarkTheme

@Composable
fun HomeworkScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Switch(
                checked         = isDarkTheme,
                onCheckedChange = { isDarkTheme = it }
            )
            Text("Welcome to the home screen", style = MaterialTheme.typography.bodyMedium)
        }
    }
}