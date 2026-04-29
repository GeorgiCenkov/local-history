package com.example.localhistory.data.repository

import com.example.localhistory.data.remote.LandmarkService
import com.example.localhistory.model.request.LandmarkRequest
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.model.response.LandmarkVisitDTO

sealed class LandmarkResult<out T> {
    data class Success<T>(val data: T) : LandmarkResult<T>()
    data class Error(val message: String) : LandmarkResult<Nothing>()
}

class LandmarkRepository(
    private val api: LandmarkService
) {

    suspend fun getTeacherLandmarks(): LandmarkResult<List<LandmarkDTO>> = runCatching {
        val response = api.getTeacherLandmarks()
        if (response.isSuccessful) {
            LandmarkResult.Success(response.body().orEmpty())
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load landmarks")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getAllLandmarks(): LandmarkResult<List<LandmarkDTO>> = runCatching {
        val response = api.getAllLandmarks()
        if (response.isSuccessful) {
            LandmarkResult.Success(response.body().orEmpty())
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load landmarks")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getLandmarkById(id: Long): LandmarkResult<LandmarkDTO> = runCatching {
        val response = api.getLandmarkById(id)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load landmark")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun createLandmark(request: LandmarkRequest): LandmarkResult<LandmarkDTO> = runCatching {
        val response = api.createLandmark(request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not create landmark")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun updateLandmark(id: Long, request: LandmarkRequest): LandmarkResult<LandmarkDTO> = runCatching {
        val response = api.updateLandmark(id, request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not update landmark")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun deleteLandmark(id: Long): LandmarkResult<Unit> = runCatching {
        val response = api.deleteLandmark(id)
        if (response.isSuccessful) {
            LandmarkResult.Success(Unit)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not delete landmark")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getVisitsForLandmark(id: Long): LandmarkResult<List<LandmarkVisitDTO>> = runCatching {
        val response = api.getVisitsForLandmark(id)
        if (response.isSuccessful) {
            LandmarkResult.Success(response.body().orEmpty())
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load visits")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getVisitById(id: Long): LandmarkResult<LandmarkVisitDTO> = runCatching {
        val response = api.getVisitById(id)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load visit")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun deleteVisit(id: Long): LandmarkResult<Unit> = runCatching {
        val response = api.deleteVisit(id)
        if (response.isSuccessful) {
            LandmarkResult.Success(Unit)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not delete visit")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }
}
