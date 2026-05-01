package com.example.localhistory.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.UserRepository
import com.example.localhistory.data.repository.UserResult
import com.example.localhistory.model.response.StudentLeaderboardDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val rows: List<StudentLeaderboardDTO> = emptyList(),
    val myRow: StudentLeaderboardDTO? = null,
    val search: String = "",
    val isLoading: Boolean = false,
    val isFindingMe: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LeaderboardUiState())
    val state: StateFlow<LeaderboardUiState> = _state

    private var searchJob: Job? = null

    init {
        loadLeaderboard()
        loadMyRow()
    }

    fun loadLeaderboard(search: String = _state.value.search) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = userRepository.getLeaderboard(search)) {
                is UserResult.Success -> _state.update {
                    it.copy(rows = result.data, isLoading = false)
                }
                is UserResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateSearch(search: String) {
        _state.update { it.copy(search = search) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250)
            loadLeaderboard(search)
        }
    }

    fun findMe() {
        viewModelScope.launch {
            _state.update { it.copy(isFindingMe = true, errorMessage = null) }
            when (val result = userRepository.getMyLeaderboardRow()) {
                is UserResult.Success -> _state.update {
                    it.copy(
                        myRow = result.data,
                        search = result.data.email,
                        isFindingMe = false
                    )
                }
                is UserResult.Error -> _state.update {
                    it.copy(isFindingMe = false, errorMessage = result.message)
                }
            }

            loadLeaderboard(_state.value.search)
        }
    }

    private fun loadMyRow() {
        viewModelScope.launch {
            when (val result = userRepository.getMyLeaderboardRow()) {
                is UserResult.Success -> _state.update { it.copy(myRow = result.data) }
                is UserResult.Error -> Unit
            }
        }
    }
}
