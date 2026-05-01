package com.example.localhistory.ui.components

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Suppress("DEPRECATION")
suspend fun searchLocation(context: Context, query: String): LatLng? =
    withContext(Dispatchers.IO) {
        runCatching {
            val address = Geocoder(context, Locale.getDefault())
                .getFromLocationName(query, 1)
                ?.firstOrNull()
                ?: return@withContext null

            LatLng(address.latitude, address.longitude)
        }.getOrNull()
    }
