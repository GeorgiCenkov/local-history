package com.example.localhistory.ui.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.data.repository.HomeworkRepository
import com.example.localhistory.data.repository.HomeworkResult
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudentHomeworkUiState(
    val assignments: List<HomeworkAssignmentDTO> = emptyList(),
    val isLoading: Boolean = false,
    val completingAssignmentId: Long? = null,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessageRes: Int? = null
)

// ViewModel for student assignment reads and explicit completion.
@HiltViewModel
class StudentHomeworkViewModel @Inject constructor(
    private val homeworkRepository: HomeworkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentHomeworkUiState())
    val state: StateFlow<StudentHomeworkUiState> = _state

    init {
        loadAssignments()
    }

    fun loadAssignments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, errorMessageRes = null) }
            when (val result = homeworkRepository.getStudentAssignments()) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(assignments = result.data, isLoading = false)
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun completeAssignment(assignment: HomeworkAssignmentDTO) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    completingAssignmentId = assignment.id,
                    errorMessage = null,
                    errorMessageRes = null,
                    successMessageRes = null
                )
            }
            when (val result = homeworkRepository.completeAssignment(assignment.id)) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(
                        assignments = it.assignments.map { item ->
                            if (item.id == result.data.id) result.data else item
                        },
                        completingAssignmentId = null,
                        successMessageRes = R.string.homework_student_complete_success
                    )
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(
                        completingAssignmentId = null,
                        errorMessageRes = R.string.homework_student_complete_error
                    )
                }
            }
        }
    }
}
