package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// If there is an error with loading a landmark
@Composable
fun LandmarkError(state: TeacherLandmarksUiState) {
    val localError = state.errorMessageRes?.let { stringResource(it) }
    val message = localError ?: state.errorMessage
    if (message != null) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.Companion.padding(vertical = 8.dp)
        )
    }
}