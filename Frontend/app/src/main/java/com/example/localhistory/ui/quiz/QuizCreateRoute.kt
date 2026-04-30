package com.example.localhistory.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun QuizCreateRoute(
    landmarkId: Long,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: QuizViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(landmarkId) {
        viewModel.startCreating(landmarkId)
    }

    QuizFormScreen(
        form = state.form,
        isLoading = state.isLoading,
        isSaving = state.isSaving,
        isEditing = false,
        errorMessage = state.errorMessage,
        errorMessageRes = state.errorMessageRes,
        onFormChange = viewModel::updateForm,
        onBack = onBack,
        onSave = { viewModel.saveQuiz { onCreated() } }
    )
}
