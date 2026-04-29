package com.example.localhistory.ui.landmark.landmarkdetails

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R
import com.example.localhistory.ui.components.LoadingContent

// Separate it from the teacher logic to keep it reusable for students
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkDetailRoute(
    landmarkId: Long,
    onBack: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    val viewModel: LandmarkDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(landmarkId) {
        viewModel.openLandmarkById(landmarkId)
    }

    val landmark = state.selectedLandmark

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = landmark?.title ?: stringResource(R.string.nav_landmark_detail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isDetailLoading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            state.errorMessage != null && landmark == null -> {
                Text(
                    text = state.errorMessage ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                )
            }

            landmark != null -> {
                LandmarkDetailScreen(
                    landmark = landmark,
                    visits = state.visits,
                    isVisitsLoading = state.isVisitsLoading,
                    errorMessage = state.errorMessage,
                    onEdit = onEdit,
                    onDelete = null,
                    onRefreshVisits = {
                        viewModel.loadVisits(landmark.id)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}