package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationPickerSheetContent(
    initialLocation: LatLng?,
    onConfirm: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    var pickedLocation by remember(initialLocation) { mutableStateOf(initialLocation) }
    val mapLocation = pickedLocation ?: defaultMapLocation()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
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


        // If there is a picked location, display it
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            CoordinateDisplay(
                latitude = mapLocation.latitude,
                longitude = mapLocation.longitude,
                title = stringResource(R.string.location_picker_marker_title),
                zoom = if (pickedLocation == null) 6f else 15f,
                showMarker = pickedLocation != null,
                onMapClick = { pickedLocation = it }
            )
        }

        Row(
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

            Button(
                onClick = { pickedLocation?.let(onConfirm) },
                enabled = pickedLocation != null
            ) {
                Text(stringResource(R.string.location_picker_confirm))
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

private fun defaultMapLocation(): LatLng = LatLng(42.6977, 23.3219)
