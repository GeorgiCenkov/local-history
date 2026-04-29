package com.example.localhistory.ui.landmark.landmarkdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LandmarkResult
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
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherLandmarksUiState())
    val state: StateFlow<TeacherLandmarksUiState> = _state

    fun openLandmarkById(id: Long) {
        _state.update {
            it.copy(
                selectedLandmark = null,
                visits = emptyList(),
                isDetailLoading = true,
                isVisitsLoading = false,
                errorMessage = null
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
}