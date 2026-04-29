package com.example.localhistory.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.localhistory.ui.components.LoadingContent
import com.example.localhistory.ui.landmark.LandmarkCard

// Main discover content for students, showing loading, empty, error, and landmark list states.
@Composable
fun DiscoverContent(
    state: DiscoverUiState,
    contentPadding: PaddingValues,
    onOpenLandmark: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
    ) {
        DiscoverError(message = state.errorMessage)

        when {
            state.isLoading && state.landmarks.isEmpty() -> LoadingContent(Modifier.fillMaxSize())
            state.landmarks.isEmpty() -> DiscoverEmptyLandmarks()
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.discover_subtitle, state.landmarks.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(state.landmarks, key = { it.id }) { landmark ->
                    LandmarkCard(
                        landmark = landmark,
                        onOpen = { onOpenLandmark(landmark.id) },
                        showOpenAction = false
                    )
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
