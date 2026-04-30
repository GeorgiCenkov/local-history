package com.example.localhistory.ui.components.landmarkfilter

import com.example.localhistory.model.response.LandmarkDTO

// Reusable function used to filter landmarks for teachers or students
fun List<LandmarkDTO>.filterByLandmarkQuery(query: String): List<LandmarkDTO> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return this

    return filter { landmark ->
        landmark.title.contains(normalizedQuery, ignoreCase = true) ||
            landmark.description.contains(normalizedQuery, ignoreCase = true)
    }
}
