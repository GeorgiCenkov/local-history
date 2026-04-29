package com.example.localhistory.ui.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// A row in the profile info table
@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.Companion.weight(0.45f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Companion.Medium,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis,
            textAlign = TextAlign.Companion.End,
            modifier = Modifier.Companion.weight(0.55f)
        )
    }
}