package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// Full-screen location picker with a map behind a locked search/confirm sheet.
@Composable
fun LocationPickerMapScreen(
    initialLocation: LatLng?,
    onConfirm: (LatLng) -> Unit
) {
    var pickedLocation by remember(initialLocation) { mutableStateOf(initialLocation) }
    val mapLocation = pickedLocation ?: defaultLocationPickerMapLocation()
    val markerState = rememberMarkerState(position = mapLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mapLocation,
            if (pickedLocation == null) 6f else 15f
        )
    }
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(mapLocation.latitude, mapLocation.longitude, pickedLocation != null) {
        markerState.position = mapLocation
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                mapLocation,
                if (pickedLocation == null) 6f else 15f
            )
        )
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { pickedLocation = it }
            ) {
                pickedLocation?.let {
                    Marker(
                        state = markerState,
                        title = null
                    )
                }
            }

            LocationPickerLockedSheet(
                pickedLocation = pickedLocation,
                onLocationFound = { pickedLocation = it },
                onConfirm = { pickedLocation?.let(onConfirm) },
                backgroundColor = backgroundColor
            )
        }
    }
}

private fun defaultLocationPickerMapLocation(): LatLng = LatLng(42.6977, 23.3219)
