package com.example.localhistory.ui.landmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.model.Coordinates
import com.example.localhistory.data.repository.ImageUploadRepository
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.model.request.LandmarkRequest
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.model.response.LandmarkVisitDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LandmarkFormState(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val selectedImageUri: String? = null,
    val latitude: String = "",
    val longitude: String = "",
    val rewardPoints: String = ""
)

data class TeacherLandmarksUiState(
    val landmarks: List<LandmarkDTO> = emptyList(),
    val selectedLandmark: LandmarkDTO? = null,
    val visits: List<LandmarkVisitDTO> = emptyList(),
    val form: LandmarkFormState = LandmarkFormState(),
    val isLoading: Boolean = false,
    val isDetailLoading: Boolean = false,
    val isVisitsLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isFormOpen: Boolean = false,
    val errorMessageRes: Int? = null,
    val errorMessage: String? = null
)


// View model for handling CRUD of landmarks
@HiltViewModel
class TeacherLandmarksViewModel @Inject constructor(
    private val repository: LandmarkRepository,
    private val imageUploadRepository: ImageUploadRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherLandmarksUiState())
    val state: StateFlow<TeacherLandmarksUiState> = _state

    init {
        loadLandmarks()
    }

    fun loadLandmarks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getTeacherLandmarks()) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(landmarks = result.data, isLoading = false)
                }
                is LandmarkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateForm(form: LandmarkFormState) {
        _state.update { it.copy(form = form, errorMessage = null, errorMessageRes = null) }
    }

    fun openLandmark(landmark: LandmarkDTO) {
        _state.update {
            it.copy(
                selectedLandmark = landmark,
                visits = emptyList(),
                isDetailLoading = true,
                isVisitsLoading = true,
                errorMessage = null,
                errorMessageRes = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.getLandmarkById(landmark.id)) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(selectedLandmark = result.data, isDetailLoading = false)
                }
                is LandmarkResult.Error -> _state.update {
                    it.copy(isDetailLoading = false, errorMessage = result.message)
                }
            }

            loadVisits(landmark.id)
        }
    }

    fun closeLandmark() {
        _state.update {
            it.copy(
                selectedLandmark = null,
                visits = emptyList(),
                isDetailLoading = false,
                isVisitsLoading = false,
                errorMessage = null,
                errorMessageRes = null
            )
        }
    }

    fun refreshSelectedLandmark() {
        val landmark = _state.value.selectedLandmark ?: return
        openLandmark(landmark)
    }

    fun startCreating() {
        _state.update {
            it.copy(form = LandmarkFormState(), isFormOpen = true, errorMessage = null)
        }
    }

    fun startEditing(landmark: LandmarkDTO) {
        _state.update {
            it.copy(
                form = LandmarkFormState(
                    id = landmark.id,
                    title = landmark.title,
                    description = landmark.description,
                    imageUrl = landmark.imageUrl,
                    selectedImageUri = null,
                    latitude = landmark.coordinates.latitude.toString(),
                    longitude = landmark.coordinates.longitude.toString(),
                    rewardPoints = landmark.visitRewardPoints.toString()
                ),
                isFormOpen = true,
                errorMessage = null
            )
        }
    }

    fun dismissForm() {
        _state.update { it.copy(isFormOpen = false, form = LandmarkFormState()) }
    }

    fun saveLandmark(onSaved: (LandmarkDTO) -> Unit = {}) {
        val form = _state.value.form
        val latitude = form.latitude.toDoubleOrNull()
        val longitude = form.longitude.toDoubleOrNull()
        val rewardPoints = form.rewardPoints.toIntOrNull()

        if (form.title.isBlank() || form.description.isBlank() ||
            (form.imageUrl.isBlank() && form.selectedImageUri.isNullOrBlank()) ||
            latitude == null || longitude == null || rewardPoints == null
        ) {
            _state.update { it.copy(errorMessageRes = R.string.landmark_error_invalid_form) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, errorMessageRes = null) }

            val imageUrl = when (val selectedImageUri = form.selectedImageUri) {
                null -> form.imageUrl.trim()
                else -> {
                    // Landmark saves must store a public image URL. If the user picked a local
                    // image, upload it first through the signed URL flow and use the returned URL.
                    when (val uploadResult = imageUploadRepository.uploadImage(selectedImageUri)) {
                        is LandmarkResult.Success -> uploadResult.data
                        is LandmarkResult.Error -> {
                            _state.update {
                                it.copy(
                                    isSaving = false,
                                    errorMessageRes = R.string.landmark_error_image_upload_failed
                                )
                            }
                            return@launch
                        }
                    }
                }
            }

            val request = LandmarkRequest(
                title = form.title.trim(),
                description = form.description.trim(),
                imageUrl = imageUrl,
                coordinates = Coordinates(latitude = latitude, longitude = longitude),
                visitRewardPoints = rewardPoints
            )

            val result = form.id?.let { repository.updateLandmark(it, request) }
                ?: repository.createLandmark(request)

            when (result) {
                is LandmarkResult.Success -> {
                    // The create/edit screens are separate navigation destinations, so they can have
                    // a different Hilt ViewModel instance from the teacher list. Update this instance
                    // immediately from the save response, then notify the route only after success.
                    _state.update {
                        val selected = it.selectedLandmark
                        val savedLandmark = result.data
                        val updatedLandmarks = if (it.landmarks.any { landmark -> landmark.id == savedLandmark.id }) {
                            it.landmarks.map { landmark ->
                                if (landmark.id == savedLandmark.id) savedLandmark else landmark
                            }
                        } else {
                            listOf(savedLandmark) + it.landmarks
                        }

                        it.copy(
                            landmarks = updatedLandmarks,
                            selectedLandmark = selected?.takeIf { landmark -> landmark.id != savedLandmark.id }
                                ?: savedLandmark,
                            isSaving = false,
                            isFormOpen = false,
                            form = LandmarkFormState()
                        )
                    }
                    onSaved(result.data)

                    // Refresh in the background as a follow-up so server-computed fields stay authoritative.
                    loadLandmarks()
                }
                is LandmarkResult.Error -> _state.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun deleteLandmark(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.deleteLandmark(id)) {
                is LandmarkResult.Success -> {
                    _state.update {
                        it.copy(
                            selectedLandmark = it.selectedLandmark?.takeIf { landmark -> landmark.id != id },
                            visits = if (it.selectedLandmark?.id == id) emptyList() else it.visits
                        )
                    }
                    loadLandmarks()
                }
                is LandmarkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun openLandmarkById(id: Long) {
        _state.update {
            it.copy(
                selectedLandmark = null,
                visits = emptyList(),
                isDetailLoading = true,
                isVisitsLoading = true,
                errorMessage = null,
                errorMessageRes = null
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

    fun loadVisits(landmarkId: Long? = _state.value.selectedLandmark?.id) {
        val id = landmarkId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isVisitsLoading = true, errorMessage = null, errorMessageRes = null) }
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

    fun deleteVisit(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isVisitsLoading = true, errorMessage = null, errorMessageRes = null) }
            when (val result = repository.deleteVisit(id)) {
                is LandmarkResult.Success -> {
                    val selectedId = _state.value.selectedLandmark?.id
                    if (selectedId != null) loadVisits(selectedId)
                }
                is LandmarkResult.Error -> _state.update {
                    it.copy(isVisitsLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}
