package com.example.localhistory.model.request

import com.example.localhistory.model.Coordinates

data class LandmarkRequest(
    val title: String,
    val description: String,
    val imageUrl: String,
    val coordinates: Coordinates,
    val visitRewardPoints: Int
)
