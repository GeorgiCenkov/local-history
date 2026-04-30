package com.example.localhistory.ui.landmark.landmarkdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.data.repository.AuthRepository
import com.example.localhistory.data.repository.ImageUploadRepository
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.data.repository.LocationRepository
import com.example.localhistory.data.repository.QuizRepository
import com.example.localhistory.model.request.LandmarkVisitRequest
import com.example.localhistory.ui.landmark.TeacherLandmarksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Use a separate view model from the main TeacherLandmarksViewmodel, so that the details view is reusable between teacher and student
@HiltViewModel
class LandmarkDetailViewModel @Inject constructor(
    private val repository: LandmarkRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherLandmarksUiState())
    val state: StateFlow<TeacherLandmarksUiState> = _state

    fun openLandmarkById(id: Long) {
        _state.update {
            it.copy(
                selectedLandmark = null,
                visits = emptyList(),
                teacherQuiz = null,
                publicQuiz = null,
                isDetailLoading = true,
                isVisitsLoading = false,
                isQuizLoading = false,
                shouldPromptQuiz = false,
                errorMessage = null,
                errorMessageRes = null,
                successMessageRes = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.getLandmarkById(id)) {
                is LandmarkResult.Success -> {
                    _state.update {
                        it.copy(
                            selectedLandmark = result.data,
                            isDetailLoading = false
                        )
                    }

                    loadVisits(id)
                }

                is LandmarkResult.Error -> {
                    _state.update {
                        it.copy(
                            isDetailLoading = false,
                            isVisitsLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun loadVisits(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isVisitsLoading = true) }

            when (val result = repository.getVisitsForLandmark(id)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(visits = result.data, isVisitsLoading = false)
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isVisitsLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun loadTeacherQuiz(landmarkId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isQuizLoading = true) }

            when (val result = quizRepository.getTeacherQuizByLandmarkId(landmarkId)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(teacherQuiz = result.data, isQuizLoading = false)
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isQuizLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun loadPublicQuiz(landmarkId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isQuizLoading = true) }

            when (val result = quizRepository.getPublicQuizByLandmarkId(landmarkId)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(
                        publicQuiz = result.data,
                        isQuizLoading = false
                    )
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isQuizLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun deleteTeacherQuiz(quizId: Long, landmarkId: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isQuizLoading = true,
                    errorMessage = null,
                    errorMessageRes = null,
                    successMessageRes = null
                )
            }

            when (val result = quizRepository.deleteQuiz(quizId)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(
                        teacherQuiz = null,
                        isQuizLoading = false,
                        successMessageRes = R.string.quiz_delete_success
                    )
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(isQuizLoading = false, errorMessage = result.message)
                }
            }

            loadTeacherQuiz(landmarkId)
        }
    }

    // Tries to submit a visit
    fun submitVisit(landmarkId: Long, imageUri: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmittingVisit = true,
                    errorMessage = null,
                    errorMessageRes = null,
                    successMessageRes = null
                )
            }

            // A valid visit needs the student's real current location and an uploaded proof image.
            // Build those first, then send the backend only stable values: landmark id, public image URL, coordinates.
            val coordinates = when (val locationResult = locationRepository.getCurrentCoordinates()) {
                is LandmarkResult.Success -> locationResult.data
                is LandmarkResult.Error -> {
                    _state.update {
                        it.copy(
                            isSubmittingVisit = false,
                            errorMessageRes = R.string.landmark_visit_error_location
                        )
                    }
                    return@launch
                }
            }

            val imageUrl = when (val uploadResult = imageUploadRepository.uploadImage(imageUri)) {
                is LandmarkResult.Success -> uploadResult.data
                is LandmarkResult.Error -> {
                    _state.update {
                        it.copy(
                            isSubmittingVisit = false,
                            errorMessageRes = R.string.landmark_visit_error_upload
                        )
                    }
                    return@launch
                }
            }

            val request = LandmarkVisitRequest(
                landmarkId = landmarkId,
                image = imageUrl,
                coordinates = coordinates
            )

            when (val submitResult = repository.submitVisit(request)) {
                is LandmarkResult.Success -> {
                    // The backend awards XP while creating the visit, but the visit response
                    // does not contain the updated StudentDTO. Refresh auth so Profile sees
                    // the new level/points stored in AuthDataStore.
                    authRepository.refreshSession()

                    _state.update {
                        it.copy(
                            visits = listOf(submitResult.data) + it.visits,
                            isSubmittingVisit = false,
                            shouldPromptQuiz = true,
                            successMessageRes = R.string.landmark_visit_submit_success
                        )
                    }
                    loadPublicQuiz(landmarkId)
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(
                        isSubmittingVisit = false,
                        errorMessageRes = submitResult.message.toVisitSubmitErrorRes()
                    )
                }
            }
        }
    }

    fun reportVisitPermissionDenied() {
        _state.update {
            it.copy(
                errorMessage = null,
                errorMessageRes = R.string.landmark_visit_error_permissions,
                successMessageRes = null
            )
        }
    }

    fun dismissVisitMessage() {
        _state.update {
            it.copy(
                errorMessage = null,
                errorMessageRes = null,
                successMessageRes = null
            )
        }
    }

    fun dismissQuizPrompt() {
        _state.update { it.copy(shouldPromptQuiz = false) }
    }

    // Backend validation returns this phrase for visits outside the configured radius.
    private fun String.toVisitSubmitErrorRes(): Int =
        if (contains("within 50 meters", ignoreCase = true)) {
            R.string.landmark_visit_error_out_of_range
        } else {
            R.string.landmark_visit_error_submit
        }
}
