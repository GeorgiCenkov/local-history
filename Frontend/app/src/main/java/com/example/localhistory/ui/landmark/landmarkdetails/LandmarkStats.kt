package com.example.localhistory.ui.landmark.landmarkdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO

// Tags ( chips ) for landmark stat like reward points, visit count, and coordinates
@Composable
fun LandmarkStats(landmark: LandmarkDTO) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        stringResource(
                            R.string.landmark_reward_points,
                            landmark.visitRewardPoints
                        )
                    )
                },
                leadingIcon = { Icon(Icons.Outlined.Star, contentDescription = null) }
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        stringResource(
                            R.string.landmark_visit_count,
                            landmark.visitsCount
                        )
                    )
                },
                leadingIcon = { Icon(Icons.Outlined.Visibility, contentDescription = null) }
            )
        }
        AssistChip(
            onClick = {},
            label = {
                Text(
                    stringResource(
                        R.string.landmark_coordinates,
                        landmark.coordinates.latitude,
                        landmark.coordinates.longitude
                    )
                )
            },
            leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) }
        )
    }
}