package com.example.localhistory.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun QuizTakeRoute(
    quizId: Long,
    onBack: () -> Unit
) {
    val viewModel: QuizTakeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    QuizTakeScreen(
        state = state,
        onBack = onBack,
        onAnswerChange = viewModel::updateAnswer,
        onPrevious = viewModel::goToPreviousQuestion,
        onNext = viewModel::goToNextQuestion,
        onSubmit = viewModel::submitQuiz
    )
}
