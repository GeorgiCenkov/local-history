package com.example.localhistory.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun QuizEditRoute(
    quizId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: QuizViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    QuizFormScreen(
        form = state.form,
        isLoading = state.isLoading,
        isSaving = state.isSaving,
        isEditing = true,
        errorMessage = state.errorMessage,
        errorMessageRes = state.errorMessageRes,
        onFormChange = viewModel::updateForm,
        onBack = onBack,
        onSave = { viewModel.saveQuiz { onSaved() } }
    )
}
