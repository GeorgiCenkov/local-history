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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.localhistory.R

// Main Screen for teacher to CRUD their landmarks
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLandmarksScreen() {
    val viewModel: TeacherLandmarksViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedLandmark = state.selectedLandmark

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
                        FilledTonalIconButton(onClick = viewModel::startCreating) {
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
            // Open the details screen or stay in the list view if nothing is selected
            if (selectedLandmark == null) {
                LandmarkListContent(
                    state = state,
                    onCreate = viewModel::startCreating,
                    onOpen = viewModel::openLandmark,
                    onEdit = viewModel::startEditing,
                    onDelete = { viewModel.deleteLandmark(it.id) }
                )
            } else {
                LandmarkDetailContent(
                    state = state,
                    landmark = selectedLandmark,
                    onEdit = { viewModel.startEditing(selectedLandmark) },
                    onDelete = { viewModel.deleteLandmark(selectedLandmark.id) },
                    onRefreshVisits = { viewModel.loadVisits(selectedLandmark.id) }
                )
            }
        }
    }

    // if we are creating / editing a landmark
    if (state.isFormOpen) {
        LandmarkFormDialog(
            form = state.form,
            isSaving = state.isSaving,
            onFormChange = viewModel::updateForm,
            onDismiss = viewModel::dismissForm,
            onSave = viewModel::saveLandmark
        )
    }
}

