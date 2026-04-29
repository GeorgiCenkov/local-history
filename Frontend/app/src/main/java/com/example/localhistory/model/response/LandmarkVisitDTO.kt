package com.example.localhistory.model.response

import com.example.localhistory.model.Coordinates
import kotlinx.datetime.LocalDateTime

data class LandmarkVisitDTO(
    val id: Long,
    val userId: Long,
    val landmarkId: Long,
    val dateVisited: LocalDateTime?,
    val image: String?,
    val coordinates: Coordinates
)
