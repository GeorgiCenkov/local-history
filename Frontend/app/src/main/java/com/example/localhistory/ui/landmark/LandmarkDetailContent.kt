package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.ui.components.LoadingContent
import com.example.localhistory.ui.components.NetworkImage

// Detailed view of a single landmark
@Composable
fun LandmarkDetailContent(
    state: TeacherLandmarksUiState,
    landmark: LandmarkDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefreshVisits: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item { LandmarkError(state) }

        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Column {
                    NetworkImage(
                        imageUrl = landmark.imageUrl,
                        contentDescription = landmark.title,
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                    Column(Modifier.Companion.padding(16.dp)) {
                        Text(
                            text = landmark.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.Companion.height(8.dp))
                        Text(
                            text = landmark.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.Companion.height(14.dp))
                        LandmarkStats(landmark)
                        Spacer(Modifier.Companion.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilledTonalButton(onClick = onEdit) {
                                Icon(Icons.Outlined.Edit, contentDescription = null)
                                Spacer(Modifier.Companion.width(8.dp))
                                Text(stringResource(R.string.action_edit))
                            }
                            TextButton(onClick = onDelete) {
                                Icon(Icons.Outlined.Delete, contentDescription = null)
                                Spacer(Modifier.Companion.width(8.dp))
                                Text(stringResource(R.string.action_delete))
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Column(modifier = Modifier.Companion.weight(1f)) {
                    Text(
                        text = stringResource(R.string.landmark_visits_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(R.string.landmark_visits_subtitle, state.visits.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRefreshVisits) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = stringResource(R.string.action_refresh)
                    )
                }
            }
        }

        when {
            state.isVisitsLoading && state.visits.isEmpty() -> item {
                LoadingContent(
                    Modifier.Companion.height(
                        160.dp
                    )
                )
            }

            state.visits.isEmpty() -> item { EmptyVisits() }
            else -> items(state.visits, key = { it.id }) { visit ->
                VisitCard(visit = visit)
            }
        }

        item { Spacer(Modifier.Companion.height(12.dp)) }
    }
}