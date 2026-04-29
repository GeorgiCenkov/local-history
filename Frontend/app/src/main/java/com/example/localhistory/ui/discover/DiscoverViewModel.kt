package com.example.localhistory.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.model.response.LandmarkDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverUiState(
    val landmarks: List<LandmarkDTO> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// View model for the student discover tab, loading the shared landmark list through the repository.
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state

    init {
        loadLandmarks()
    }

    fun loadLandmarks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.getAllLandmarks()) {
                is LandmarkResult.Success -> _state.update {
                    it.copy(
                        landmarks = result.data,
                        isLoading = false
                    )
                }

                is LandmarkResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
