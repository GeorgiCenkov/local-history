package com.example.localhistory.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

// Basic container for loading any network image
@Composable
fun NetworkImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Companion.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                Icons.Outlined.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl.trim())
                    .crossfade(true)
                    .listener(
                        onStart = {
                            Log.d("COIL_DEBUG", "Loading: $imageUrl")
                        },
                        onSuccess = { _, result ->
                            Log.d("COIL_DEBUG", "Success: ${result.request.data}")
                        },
                        onError = { _, result ->
                            Log.e("COIL_DEBUG", "Error loading: $imageUrl", result.throwable)
                        }
                    )
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Companion.Crop,
                modifier = Modifier.Companion.fillMaxSize()
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Error -> {
                        Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                            Icon(Icons.Outlined.Image, contentDescription = null)
                            Text("Image failed")
                        }
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        }
    }
}