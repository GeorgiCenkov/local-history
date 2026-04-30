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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.ui.components.landmarkfilter.LandmarkFilterRow
import com.example.localhistory.ui.components.landmarkfilter.filterByLandmarkQuery
import com.example.localhistory.ui.components.LoadingContent
import com.example.localhistory.ui.landmark.LandmarkCard

// Main discover content for students, showing loading, empty, error, and landmark list states.
@Composable
fun DiscoverContent(
    state: DiscoverUiState,
    contentPadding: PaddingValues,
    onOpenLandmark: (Long) -> Unit
) {
    var filterQuery by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    val filteredLandmarks = state.landmarks.filterByLandmarkQuery(filterQuery)

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
            else -> {
                // Filters
                LandmarkFilterRow(
                    query = filterQuery,
                    onQueryChange = { filterQuery = it },
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.discover_subtitle, filteredLandmarks.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    if (filteredLandmarks.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.landmark_filter_no_results),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(filteredLandmarks, key = { it.id }) { landmark ->
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
}
