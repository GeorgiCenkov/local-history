package com.example.localhistory.ui.landmark.landmarkdetails

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.localhistory.R
import java.io.File

// Visible only to students
// Handles prompting to take a picture and requesting necessary permissions, then returns the photo URI to the caller
@Composable
fun VisitPhotoCaptureButton(
    isSubmitting: Boolean,
    onPhotoCaptured: (String) -> Unit,
    onPermissionDenied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val photoUri = pendingPhotoUri
        if (success && photoUri != null) {
            onPhotoCaptured(photoUri.toString())
        }
        pendingPhotoUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasCamera = permissions[Manifest.permission.CAMERA] == true
        val hasFineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasCoarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasCamera && (hasFineLocation || hasCoarseLocation)) {
            val uri = context.createVisitPhotoUri()
            pendingPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            onPermissionDenied()
        }
    }

    FilledTonalButton(
        onClick = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        enabled = !isSubmitting,
        modifier = modifier
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(strokeWidth = 2.dp)
        } else {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
        }
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.landmark_visit_submit_action))
    }
}

private fun Context.createVisitPhotoUri(): Uri {
    val directory = File(cacheDir, "visit_photos").apply { mkdirs() }
    val file = File.createTempFile("visit_", ".jpg", directory)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}
