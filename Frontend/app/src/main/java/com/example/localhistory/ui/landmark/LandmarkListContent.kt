package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.ui.components.LoadingContent

// Displays all landmark cards in a column
@Composable
fun LandmarkListContent(
    state: TeacherLandmarksUiState,
    onCreate: () -> Unit,
    onOpen: (LandmarkDTO) -> Unit,
    onEdit: (LandmarkDTO) -> Unit,
    onDelete: (LandmarkDTO) -> Unit
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LandmarkError(state)

        when {
            state.isLoading && state.landmarks.isEmpty() -> LoadingContent()
            state.landmarks.isEmpty() -> EmptyLandmarks(onCreate = onCreate)
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.Companion.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.landmarks_subtitle, state.landmarks.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.Companion.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                items(state.landmarks, key = { it.id }) { landmark ->
                    LandmarkCard(
                        landmark = landmark,
                        onOpen = { onOpen(landmark) },
                        onEdit = { onEdit(landmark) },
                        onDelete = { onDelete(landmark) }
                    )
                }
                item { Spacer(Modifier.Companion.height(12.dp)) }
            }
        }
    }
}