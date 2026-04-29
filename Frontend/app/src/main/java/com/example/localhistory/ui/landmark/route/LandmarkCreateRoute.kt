package com.example.localhistory.ui.landmark.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.ui.landmark.LandmarkFormScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksViewModel

// Wrapper for preparing a landmark form screen for creating a new landmark
@Composable
fun LandmarkCreateRoute(
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: TeacherLandmarksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startCreating()
    }

    LandmarkFormScreen(
        form = state.form,
        isSaving = state.isSaving,
        isEditing = false,
        onFormChange = viewModel::updateForm,
        onBack = onBack,
        onSave = {
            viewModel.saveLandmark()
            onCreated()
        }
    )
}