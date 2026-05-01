package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// Locked bottom sheet for searching and confirming the full-screen map selection.
@Composable
fun BoxScope.LocationPickerLockedSheet(
    pickedLocation: LatLng?,
    onLocationFound: (LatLng) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    backgroundColor: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchError by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(
        color = Color(backgroundColor),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 24.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .align(Alignment.BottomCenter)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.location_picker_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.location_picker_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchError = null
                },
                label = { Text(stringResource(R.string.location_picker_search_label)) },
                placeholder = { Text(stringResource(R.string.location_picker_search_placeholder)) },
                singleLine = true,
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isSearching = true
                        searchError = null
                        val result = searchLocation(context, searchQuery)
                        isSearching = false

                        if (result == null) {
                            searchError = context.getString(R.string.location_picker_search_error)
                        } else {
                            onLocationFound(result)
                        }
                    }
                },
                enabled = searchQuery.isNotBlank() && !isSearching,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.location_picker_search_action))
            }

            searchError?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pickedLocation?.let {
                        stringResource(
                            R.string.landmark_coordinates,
                            it.latitude,
                            it.longitude
                        )
                    } ?: stringResource(R.string.location_picker_no_selection),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = onCancel) {
                    Text(stringResource(R.string.action_cancel))
                }

                Button(
                    onClick = onConfirm,
                    enabled = pickedLocation != null
                ) {
                    Text(stringResource(R.string.location_picker_confirm))
                }
            }
        }
    }
}
