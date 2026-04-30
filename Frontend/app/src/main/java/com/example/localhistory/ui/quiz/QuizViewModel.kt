package com.example.localhistory.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.data.repository.QuizRepository
import com.example.localhistory.model.request.QuizQuestionRequest
import com.example.localhistory.model.request.QuizRequest
import com.example.localhistory.model.response.QuizDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestionFormState(
    val question: String = "",
    val options: List<String> = listOf("", ""),
    val correctAnswer: String = ""
)

data class QuizFormState(
    val id: Long? = null,
    val landmarkId: Long = 0,
    val title: String = "",
    val questions: List<QuizQuestionFormState> = listOf(QuizQuestionFormState())
)

data class QuizUiState(
    val form: QuizFormState = QuizFormState(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state

    fun startCreating(landmarkId: Long) {
        _state.update {
            it.copy(
                form = QuizFormState(landmarkId = landmarkId),
                isLoading = false,
                isSaving = false,
                errorMessage = null,
                errorMessageRes = null
            )
        }
    }

    fun loadQuiz(quizId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, errorMessageRes = null) }

            when (val result = repository.getTeacherQuizById(quizId)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(
                        form = result.data.toFormState(),
                        isLoading = false
                    )
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateForm(form: QuizFormState) {
        _state.update { it.copy(form = form, errorMessage = null, errorMessageRes = null) }
    }

    fun saveQuiz(onSaved: (QuizDTO) -> Unit) {
        val form = _state.value.form
        val request = form.toRequestOrNull()

        if (request == null) {
            _state.update { it.copy(errorMessageRes = R.string.quiz_error_invalid_form) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, errorMessageRes = null) }

            val result = form.id?.let { repository.updateQuiz(it, request) }
                ?: repository.createQuiz(request)

            when (result) {
                is LandmarkResult.Success -> {
                    _state.update { it.copy(isSaving = false, form = result.data.toFormState()) }
                    onSaved(result.data)
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun QuizDTO.toFormState(): QuizFormState =
        QuizFormState(
            id = id,
            landmarkId = landmarkId,
            title = title,
            questions = questions.map {
                QuizQuestionFormState(
                    question = it.question,
                    options = it.options,
                    correctAnswer = it.correctAnswer
                )
            }.ifEmpty { listOf(QuizQuestionFormState()) }
        )

    private fun QuizFormState.toRequestOrNull(): QuizRequest? {
        val cleanTitle = title.trim()
        val cleanQuestions = questions.map { question ->
            val cleanQuestion = question.question.trim()
            val cleanOptions = question.options.map { it.trim() }.filter { it.isNotBlank() }
            val cleanCorrectAnswer = question.correctAnswer.trim()

            if (cleanQuestion.isBlank() || cleanOptions.size < 2 || cleanCorrectAnswer.isBlank()) {
                return null
            }

            if (cleanCorrectAnswer !in cleanOptions) {
                return null
            }

            QuizQuestionRequest(
                question = cleanQuestion,
                options = cleanOptions,
                correctAnswer = cleanCorrectAnswer
            )
        }

        if (cleanTitle.isBlank() || landmarkId <= 0 || cleanQuestions.isEmpty()) {
            return null
        }

        return QuizRequest(
            title = cleanTitle,
            landmarkId = landmarkId,
            questions = cleanQuestions
        )
    }
}
