package com.example.localhistory.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.data.repository.AuthRepository
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.data.repository.QuizRepository
import com.example.localhistory.model.request.QuizQuestionAnswerRequest
import com.example.localhistory.model.request.QuizSubmissionRequest
import com.example.localhistory.model.response.PublicQuizDTO
import com.example.localhistory.model.response.QuizSubmissionResultDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizTakeUiState(
    val quiz: PublicQuizDTO? = null,
    val answers: Map<Long, String> = emptyMap(),
    val currentQuestionIndex: Int = 0,
    val result: QuizSubmissionResultDTO? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null
)

@HiltViewModel
class QuizTakeViewModel @Inject constructor(
    private val repository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuizTakeUiState())
    val state: StateFlow<QuizTakeUiState> = _state

    fun loadQuiz(quizId: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    result = null,
                    errorMessage = null,
                    errorMessageRes = null
                )
            }

            when (val result = repository.getPublicQuizById(quizId)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(
                        quiz = result.data,
                        answers = result.data.questions.associate { question -> question.id to "" },
                        currentQuestionIndex = 0,
                        isLoading = false
                    )
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateAnswer(questionId: Long, answer: String) {
        _state.update {
            it.copy(
                answers = it.answers + (questionId to answer),
                errorMessage = null,
                errorMessageRes = null
            )
        }
    }

    fun goToPreviousQuestion() {
        _state.update {
            it.copy(currentQuestionIndex = (it.currentQuestionIndex - 1).coerceAtLeast(0))
        }
    }

    fun goToNextQuestion() {
        val quiz = _state.value.quiz ?: return
        val currentQuestion = quiz.questions.getOrNull(_state.value.currentQuestionIndex) ?: return

        if (_state.value.answers[currentQuestion.id].isNullOrBlank()) {
            _state.update { it.copy(errorMessageRes = R.string.quiz_take_error_select_answer) }
            return
        }

        _state.update {
            it.copy(
                currentQuestionIndex = (it.currentQuestionIndex + 1).coerceAtMost(quiz.questions.lastIndex),
                errorMessageRes = null
            )
        }
    }

    fun submitQuiz() {
        val quiz = _state.value.quiz ?: return
        val answers = quiz.questions.map { question ->
            QuizQuestionAnswerRequest(
                questionId = question.id,
                answer = _state.value.answers[question.id].orEmpty()
            )
        }

        if (answers.any { it.answer.isBlank() }) {
            _state.update { it.copy(errorMessageRes = R.string.quiz_take_error_incomplete) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    errorMessageRes = null
                )
            }

            when (val result = repository.submitQuiz(quiz.id, QuizSubmissionRequest(answers))) {
                is LandmarkResult.Success -> {
                    // Quiz submission can award XP, so refresh the stored user session
                    // before the profile screen reads the student's level and points again.
                    authRepository.refreshSession()
                    _state.update {
                        it.copy(isSubmitting = false, result = result.data)
                    }
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isSubmitting = false, errorMessage = result.message)
                }
            }
        }
    }
}
