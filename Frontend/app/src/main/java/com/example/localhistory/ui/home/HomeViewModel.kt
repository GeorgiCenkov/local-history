package com.example.localhistory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.HomeworkRepository
import com.example.localhistory.data.repository.HomeworkResult
import com.example.localhistory.data.repository.UserRepository
import com.example.localhistory.data.repository.UserResult
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.model.response.StudentLeaderboardDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudentHomeUiState(
    val assignments: List<HomeworkAssignmentDTO> = emptyList(),
    val myRank: StudentLeaderboardDTO? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeworkRepository: HomeworkRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StudentHomeUiState())
    val state: StateFlow<StudentHomeUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = homeworkRepository.getStudentAssignments()) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(assignments = result.data)
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(errorMessage = result.message)
                }
            }

            when (val result = userRepository.getMyLeaderboardRow()) {
                is UserResult.Success -> _state.update {
                    it.copy(myRank = result.data, isLoading = false)
                }
                is UserResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = it.errorMessage ?: result.message)
                }
            }
        }
    }
}
