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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val currentOnPhotoCaptured by rememberUpdatedState(onPhotoCaptured)
    val currentOnPermissionDenied by rememberUpdatedState(onPermissionDenied)
    var pendingPhotoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var shouldLaunchCamera by rememberSaveable { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val photoUriString = pendingPhotoUriString
        if (success && photoUriString != null) {
            currentOnPhotoCaptured(photoUriString)
        }
        pendingPhotoUriString = null
    }

    fun createAndStorePhotoUri(): Uri {
        val uri = context.createVisitPhotoUri()
        pendingPhotoUriString = uri.toString()
        return uri
    }

    fun launchCameraWithStoredUri() {
        // The permission dialog and camera activity can recreate the composition on some devices.
        // Keep the URI as a saveable string so the result callback still has proof to submit.
        val uri = pendingPhotoUriString?.let(Uri::parse) ?: createAndStorePhotoUri()
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(shouldLaunchCamera, pendingPhotoUriString) {
        if (shouldLaunchCamera) {
            shouldLaunchCamera = false
            launchCameraWithStoredUri()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Android may omit already-granted permissions from the callback map,
        // so re-check the real permission state before deciding what to do.
        if (context.hasVisitSubmissionPermissions()) {
            shouldLaunchCamera = true
        } else {
            shouldLaunchCamera = false
            pendingPhotoUriString = null
            currentOnPermissionDenied()
        }
    }

    FilledTonalButton(
        onClick = {
            createAndStorePhotoUri()
            if (context.hasVisitSubmissionPermissions()) {
                shouldLaunchCamera = true
            } else {
                shouldLaunchCamera = false
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
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

private fun Context.hasVisitSubmissionPermissions(): Boolean {
    val hasCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED

    return hasCamera && (hasFineLocation || hasCoarseLocation)
}

private fun Context.createVisitPhotoUri(): Uri {
    val directory = File(cacheDir, "visit_photos").apply { mkdirs() }
    val file = File.createTempFile("visit_", ".jpg", directory)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}
