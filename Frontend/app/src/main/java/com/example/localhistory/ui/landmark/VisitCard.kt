package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkVisitDTO
import com.example.localhistory.ui.components.NetworkImage

// Used in the detailed landmark view to display a single visit to that landmark
@Composable
fun VisitCard(
    visit: LandmarkVisitDTO
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Column {
            // Heading image
            if (!visit.image.isNullOrBlank()) {
                NetworkImage(
                    imageUrl = visit.image,
                    contentDescription = stringResource(R.string.landmark_visit_image),
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }
            Row(
                modifier = Modifier.Companion.padding(16.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Column(modifier = Modifier.Companion.weight(1f)) {
                    Text(
                        text = stringResource(R.string.landmark_visit_user, visit.userId),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(
                            R.string.landmark_visit_date,
                            visit.dateVisited?.toString() ?: stringResource(R.string.common_unknown)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.landmark_coordinates,
                            visit.coordinates.latitude,
                            visit.coordinates.longitude
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}