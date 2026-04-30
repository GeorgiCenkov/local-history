package com.example.localhistory.ui.landmark.landmarkdetails

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.ui.components.CoordinateDisplay
import androidx.core.net.toUri


@Composable
fun LandmarkMapSheetContent(
    landmark: LandmarkDTO,
    modifier: Modifier = Modifier,
    onOpenDirections: () -> Unit
) {
    val context = LocalContext.current
    val latitude = landmark.coordinates.latitude
    val longitude = landmark.coordinates.longitude

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.landmark_map_title),
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            CoordinateDisplay(
                latitude = latitude,
                longitude = longitude,
                title = landmark.title
            )
        }

        Button(
            onClick = {
                context.openGoogleMapsDirections(latitude, longitude)
                onOpenDirections()
            },
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.Directions, contentDescription = null)
            Spacer(Modifier.Companion.width(8.dp))
            Text(stringResource(R.string.landmark_action_open_directions))
        }

        Spacer(Modifier.Companion.height(8.dp))
    }
}

private fun android.content.Context.openGoogleMapsDirections(
    latitude: Double,
    longitude: Double
) {
    val navigationUri = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude".toUri()
    val googleMapsIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        startActivity(googleMapsIntent)
    } catch (_: ActivityNotFoundException) {
        val fallbackUri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
        startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
    }
}
