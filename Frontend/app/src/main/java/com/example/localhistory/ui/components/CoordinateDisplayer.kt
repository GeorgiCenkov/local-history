package com.example.localhistory.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// Simple google maps display implementation to show a location based on coordinates. Used in the LocationDetailsScreen.
@Composable
fun CoordinateDisplay(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
    title: String = "Location",
    zoom: Float = 15f,
    showMarker: Boolean = true,
    onMapClick: ((LatLng) -> Unit)? = null
) {
    val location = LatLng(latitude, longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, zoom)
    }
    val markerState = rememberMarkerState(position = location)

    LaunchedEffect(location, zoom) {
        markerState.position = location
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }

    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { onMapClick?.invoke(it) }
        ) {
            if (showMarker) {
                Marker(
                    state = markerState,
                    title = title
                )
            }
        }
    }
}
