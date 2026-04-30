package com.example.localhistory.model.request

import com.example.localhistory.model.Coordinates

data class LandmarkVisitRequest(
    val landmarkId: Long,
    val image: String,
    val coordinates: Coordinates
)
