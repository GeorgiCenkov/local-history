package com.example.localhistory.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.example.localhistory.model.Coordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

// Used for handling the gps location of the user for submitting visits, etc
class LocationRepository(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentCoordinates(): LandmarkResult<Coordinates> {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = runCatching {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                .filter { provider -> locationManager.isProviderEnabled(provider) }
        }.getOrElse {
            return LandmarkResult.Error("Could not access location providers")
        }

        if (providers.isEmpty()) {
            return LandmarkResult.Error("Location is disabled")
        }

        val lastKnown = providers
            .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { location -> location.time }

        val location = lastKnown ?: runCatching {
            withTimeoutOrNull(15_000) {
                // Network provider usually returns faster immediately after the user grants
                // location permission; GPS can be slow on the first attempt.
                val provider = providers.firstOrNull { it == LocationManager.NETWORK_PROVIDER }
                    ?: providers.first()
                requestSingleLocation(locationManager, provider)
            }
        }.getOrNull()

        return location?.let {
            LandmarkResult.Success(Coordinates(it.latitude, it.longitude))
        } ?: LandmarkResult.Error("Could not get current location")
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private suspend fun requestSingleLocation(
        locationManager: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { continuation ->
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (continuation.isActive) continuation.resume(location)
                locationManager.removeUpdates(this)
            }

            override fun onProviderDisabled(provider: String) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        continuation.invokeOnCancellation {
            locationManager.removeUpdates(listener)
        }
    }
}
