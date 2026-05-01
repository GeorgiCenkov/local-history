package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// Shows the current homework screen error or success message in the standard compact style.
@Composable
fun HomeworkMessage(state: TeacherHomeworkUiState) {
    val error = state.errorMessageRes?.let { stringResource(it) } ?: state.errorMessage
    val success = state.successMessageRes?.let { stringResource(it) }
    val message = error ?: success ?: return

    Text(
        text = message,
        color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
