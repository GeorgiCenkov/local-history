package com.example.localhistory.ui.landmark.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.ui.landmark.LandmarkFormScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksViewModel

@Composable
fun LandmarkEditRoute(
    landmarkId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: TeacherLandmarksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(landmarkId) {
        viewModel.openLandmarkById(landmarkId)
    }

    LaunchedEffect(state.selectedLandmark) {
        state.selectedLandmark?.let {
            viewModel.startEditing(it)
        }
    }

    LandmarkFormScreen(
        form = state.form,
        isSaving = state.isSaving,
        isEditing = true,
        onFormChange = viewModel::updateForm,
        onBack = onBack,
        onSave = {
            // Navigate back only after the repository confirms the landmark was updated.
            viewModel.saveLandmark { onSaved() }
        }
    )
}
