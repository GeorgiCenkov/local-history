package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.google.android.gms.maps.model.LatLng

// Reusable landmark / coordinates picker
@Composable
fun LocationPicker(
    latitude: String,
    longitude: String,
    onLocationPicked: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLocation = remember(latitude, longitude) {
        parseLocation(latitude, longitude)
    }

    var isPickerVisible by remember { mutableStateOf(false) }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.location_picker_title),
                style = MaterialTheme.typography.titleMedium
            )


            // If nothing is selected, display empty state text, otherwise display the coordinates
            Text(
                text = selectedLocation?.let {
                    stringResource(
                        R.string.landmark_coordinates,
                        it.latitude,
                        it.longitude
                    )
                } ?: stringResource(R.string.location_picker_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            selectedLocation?.let { location ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    CoordinateDisplay(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        title = stringResource(R.string.location_picker_marker_title)
                    )
                }
            }

            FilledTonalButton(
                onClick = { isPickerVisible = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Place, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(
                        if (selectedLocation == null) R.string.location_picker_choose
                        else R.string.location_picker_change
                    )
                )
            }
        }
    }

    // Display a dedicated full-screen picker so the map has enough space.
    if (isPickerVisible) {
        LocationPickerMapScreen(
            initialLocation = selectedLocation,
            onConfirm = { location ->
                onLocationPicked(location.latitude, location.longitude)
                isPickerVisible = false
            },
            onCancel = { isPickerVisible = false }
        )
    }
}

private fun parseLocation(latitude: String, longitude: String): LatLng? {
    val lat = latitude.toDoubleOrNull() ?: return null
    val lng = longitude.toDoubleOrNull() ?: return null
    return LatLng(lat, lng)
}
