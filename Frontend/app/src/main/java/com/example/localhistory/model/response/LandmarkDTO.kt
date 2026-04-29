package com.example.localhistory.model.response

import com.example.localhistory.model.Coordinates
import kotlinx.datetime.LocalDateTime

data class LandmarkDTO(
    val id: Long,
    val title: String,
    val description: String,
    val dateCreated: LocalDateTime?,
    val imageUrl: String,
    val coordinates: Coordinates,
    val ownerId: Long?,
    val visitRewardPoints: Int,
    val visitsCount: Int
)
