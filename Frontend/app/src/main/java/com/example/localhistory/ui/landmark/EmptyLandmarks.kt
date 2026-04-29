package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R

// Rendered if the teacher has no landmarks yet. Encourages them to create one.
@Composable
fun EmptyLandmarks(onCreate: () -> Unit) {
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.Companion.size(48.dp)
        )
        Spacer(Modifier.Companion.height(12.dp))
        Text(
            stringResource(R.string.landmarks_empty_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.Companion.height(8.dp))
        Text(
            text = stringResource(R.string.landmarks_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.Companion.height(20.dp))
        Button(onClick = onCreate) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.Companion.width(8.dp))
            Text(stringResource(R.string.landmark_action_add))
        }
    }
}