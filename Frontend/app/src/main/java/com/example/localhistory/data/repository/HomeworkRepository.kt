package com.example.localhistory.data.repository

import com.example.localhistory.data.remote.HomeworkService
import com.example.localhistory.model.request.HomeworkAssignmentRequest
import com.example.localhistory.model.request.HomeworkRequest
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.model.response.HomeworkDTO
import com.example.localhistory.model.response.User

sealed class HomeworkResult<out T> {
    data class Success<T>(val data: T) : HomeworkResult<T>()
    data class Error(val message: String) : HomeworkResult<Nothing>()
}

class HomeworkRepository(
    private val api: HomeworkService
) {

    suspend fun getTeacherHomework(): HomeworkResult<List<HomeworkDTO>> = runCatching {
        val response = api.getTeacherHomework()
        if (response.isSuccessful) {
            HomeworkResult.Success(response.body().orEmpty())
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not load homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun getStudentAssignments(): HomeworkResult<List<HomeworkAssignmentDTO>> = runCatching {
        val response = api.getStudentAssignments()
        if (response.isSuccessful) {
            HomeworkResult.Success(response.body().orEmpty())
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not load homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun createHomework(request: HomeworkRequest): HomeworkResult<HomeworkDTO> = runCatching {
        val response = api.createHomework(request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            HomeworkResult.Success(body)
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not create homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun updateHomework(id: Long, request: HomeworkRequest): HomeworkResult<HomeworkDTO> = runCatching {
        val response = api.updateHomework(id, request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            HomeworkResult.Success(body)
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not update homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun deleteHomework(id: Long): HomeworkResult<Unit> = runCatching {
        val response = api.deleteHomework(id)
        if (response.isSuccessful) {
            HomeworkResult.Success(Unit)
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not delete homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun getAssignments(id: Long): HomeworkResult<List<HomeworkAssignmentDTO>> = runCatching {
        val response = api.getAssignments(id)
        if (response.isSuccessful) {
            HomeworkResult.Success(response.body().orEmpty())
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not load assignments")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun assignHomework(id: Long, studentIds: List<Long>): HomeworkResult<List<HomeworkAssignmentDTO>> = runCatching {
        val response = api.assignHomework(id, HomeworkAssignmentRequest(studentIds))
        if (response.isSuccessful) {
            HomeworkResult.Success(response.body().orEmpty())
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not assign homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun completeAssignment(id: Long): HomeworkResult<HomeworkAssignmentDTO> = runCatching {
        val response = api.completeAssignment(id)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            HomeworkResult.Success(body)
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not complete homework")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }

    suspend fun searchStudents(search: String): HomeworkResult<List<User.Student>> = runCatching {
        val response = api.searchStudents(search)
        if (response.isSuccessful) {
            HomeworkResult.Success(response.body().orEmpty().filterIsInstance<User.Student>())
        } else {
            HomeworkResult.Error(response.errorBody()?.string() ?: "Could not search students")
        }
    }.getOrElse {
        HomeworkResult.Error(it.message ?: "Network error")
    }
}
