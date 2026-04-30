package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R

// Main Screen for teacher to CRUD their landmarks
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLandmarksScreen(
    onOpenLandmark: (Long) -> Unit,
    onEditLandmark: (Long) -> Unit,
    onCreateLandmark: () -> Unit
) {
    val viewModel: TeacherLandmarksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedLandmark = state.selectedLandmark
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, selectedLandmark?.id) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Create/edit screens are separate destinations with their own ViewModel instances.
                // Refresh this screen when it becomes visible again so its list/detail state is current.
                if (selectedLandmark == null) viewModel.loadLandmarks()
                else viewModel.refreshSelectedLandmark()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedLandmark?.title ?: stringResource(R.string.landmarks_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (selectedLandmark != null) {
                        IconButton(onClick = viewModel::closeLandmark) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (selectedLandmark == null) viewModel.loadLandmarks()
                            else viewModel.refreshSelectedLandmark()
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.action_refresh)
                        )
                    }
                    if (selectedLandmark == null) {
                        FilledTonalIconButton(onClick = onCreateLandmark) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = stringResource(R.string.landmark_action_add)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LandmarkListContent(
                state = state,
                onCreate = onCreateLandmark,
                onOpen = { landmark ->
                    onOpenLandmark(landmark.id)
                },
                onEdit = { landmark ->
                    onEditLandmark(landmark.id)
                },
                onDelete = { viewModel.deleteLandmark(it.id) }
            )
        }
    }
}

