package com.example.localhistory.ui.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.R
import com.example.localhistory.data.repository.HomeworkRepository
import com.example.localhistory.data.repository.HomeworkResult
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LandmarkResult
import com.example.localhistory.model.request.HomeworkRequest
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.model.response.HomeworkDTO
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.model.response.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

data class HomeworkFormState(
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val landmarkId: Long? = null,
    val requireVisit: Boolean = true,
    val requireQuiz: Boolean = false
)

data class TeacherHomeworkUiState(
    val homework: List<HomeworkDTO> = emptyList(),
    val landmarks: List<LandmarkDTO> = emptyList(),
    val selectedHomework: HomeworkDTO? = null,
    val assignments: List<HomeworkAssignmentDTO> = emptyList(),
    val form: HomeworkFormState = HomeworkFormState(),
    val studentSearch: String = "",
    val searchResults: List<User.Student> = emptyList(),
    val selectedStudents: List<User.Student> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isAssigning: Boolean = false,
    val isSearching: Boolean = false,
    val isFormOpen: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessageRes: Int? = null
)

// ViewModel that coordinates teacher homework CRUD and bulk student assignment.
@HiltViewModel
class TeacherHomeworkViewModel @Inject constructor(
    private val homeworkRepository: HomeworkRepository,
    private val landmarkRepository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherHomeworkUiState())
    val state: StateFlow<TeacherHomeworkUiState> = _state

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        loadHomework()
        loadLandmarks()
    }

    fun loadHomework() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, errorMessageRes = null) }
            when (val result = homeworkRepository.getTeacherHomework()) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(homework = result.data, isLoading = false)
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun loadLandmarks() {
        viewModelScope.launch {
            when (val result = landmarkRepository.getTeacherLandmarks()) {
                is LandmarkResult.Success -> _state.update { it.copy(landmarks = result.data) }
                is LandmarkResult.Error -> _state.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun openCreateForm() {
        _state.update {
            it.copy(
                isFormOpen = true,
                form = HomeworkFormState(landmarkId = it.landmarks.firstOrNull()?.id),
                errorMessage = null,
                errorMessageRes = null,
                successMessageRes = null
            )
        }
    }

    fun closeForm() {
        _state.update { it.copy(isFormOpen = false, form = HomeworkFormState(), errorMessageRes = null) }
    }

    fun updateForm(form: HomeworkFormState) {
        _state.update { it.copy(form = form, errorMessage = null, errorMessageRes = null) }
    }

    fun saveHomework() {
        val form = _state.value.form
        val dueDate = form.dueDate.trim().let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val landmarkId = form.landmarkId

        if (form.title.isBlank() || form.description.isBlank() || dueDate == null ||
            landmarkId == null || (!form.requireVisit && !form.requireQuiz)
        ) {
            _state.update { it.copy(errorMessageRes = R.string.homework_error_invalid_form) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, errorMessageRes = null) }
            val request = HomeworkRequest(
                title = form.title.trim(),
                description = form.description.trim(),
                dueDate = dueDate,
                landmarkId = landmarkId,
                requireVisit = form.requireVisit,
                requireQuiz = form.requireQuiz
            )

            when (val result = homeworkRepository.createHomework(request)) {
                is HomeworkResult.Success -> {
                    _state.update {
                        it.copy(
                            homework = listOf(result.data) + it.homework,
                            selectedHomework = result.data,
                            assignments = emptyList(),
                            isSaving = false,
                            isFormOpen = false,
                            form = HomeworkFormState(),
                            successMessageRes = R.string.homework_create_success
                        )
                    }
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun deleteHomework(homeworkId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, errorMessageRes = null) }
            when (val result = homeworkRepository.deleteHomework(homeworkId)) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(
                        homework = it.homework.filterNot { item -> item.id == homeworkId },
                        selectedHomework = it.selectedHomework?.takeIf { item -> item.id != homeworkId },
                        assignments = if (it.selectedHomework?.id == homeworkId) emptyList() else it.assignments,
                        isLoading = false
                    )
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun selectHomework(homework: HomeworkDTO) {
        _state.update {
            it.copy(
                selectedHomework = homework,
                assignments = emptyList(),
                studentSearch = "",
                searchResults = emptyList(),
                selectedStudents = emptyList(),
                errorMessage = null,
                errorMessageRes = null,
                successMessageRes = null
            )
        }
        loadAssignments(homework.id)
    }

    fun clearSelection() {
        _state.update {
            it.copy(
                selectedHomework = null,
                assignments = emptyList(),
                selectedStudents = emptyList(),
                searchResults = emptyList(),
                studentSearch = ""
            )
        }
    }

    fun updateStudentSearch(search: String) {
        _state.update { it.copy(studentSearch = search, errorMessage = null, errorMessageRes = null) }
        searchJob?.cancel()

        if (search.isBlank()) {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            delay(250)
            when (val result = homeworkRepository.searchStudents(search.trim())) {
                is HomeworkResult.Success -> _state.update { current ->
                    // Hide students already picked in this assignment batch.
                    current.copy(
                        isSearching = false,
                        searchResults = result.data.filterNot { student ->
                            current.selectedStudents.any { it.id == student.id }
                        }
                    )
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isSearching = false, errorMessage = result.message)
                }
            }
        }
    }

    fun addStudent(student: User.Student) {
        _state.update {
            if (it.selectedStudents.any { selected -> selected.id == student.id }) {
                it
            } else {
                it.copy(
                    selectedStudents = it.selectedStudents + student,
                    searchResults = it.searchResults.filterNot { result -> result.id == student.id },
                    studentSearch = ""
                )
            }
        }
    }

    fun removeStudent(studentId: Long) {
        _state.update {
            it.copy(selectedStudents = it.selectedStudents.filterNot { student -> student.id == studentId })
        }
    }

    fun assignSelectedStudents() {
        val homework = _state.value.selectedHomework ?: return
        val studentIds = _state.value.selectedStudents.map { it.id }

        if (studentIds.isEmpty()) {
            _state.update { it.copy(errorMessageRes = R.string.homework_error_no_students) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAssigning = true, errorMessage = null, errorMessageRes = null) }
            when (val result = homeworkRepository.assignHomework(homework.id, studentIds)) {
                is HomeworkResult.Success -> _state.update {
                    it.copy(
                        assignments = result.data,
                        selectedStudents = emptyList(),
                        searchResults = emptyList(),
                        studentSearch = "",
                        isAssigning = false,
                        successMessageRes = R.string.homework_assign_success
                    )
                }
                is HomeworkResult.Error -> _state.update {
                    it.copy(isAssigning = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun loadAssignments(homeworkId: Long) {
        viewModelScope.launch {
            when (val result = homeworkRepository.getAssignments(homeworkId)) {
                is HomeworkResult.Success -> _state.update { it.copy(assignments = result.data) }
                is HomeworkResult.Error -> _state.update { it.copy(errorMessage = result.message) }
            }
        }
    }
}
