package com.example.localhistory.ui.landmark.route

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R
import com.example.localhistory.ui.components.LoadingContent
import com.example.localhistory.ui.landmark.LandmarkDetailScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksViewModel

// Simple wrapper for the LandmarkDetailsScreen that loads the landmark by id and handles the back and edit actions.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkDetailRoute(
    landmarkId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val viewModel: TeacherLandmarksViewModel = hiltViewModel()
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
        if (landmark == null) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LandmarkDetailScreen(
                landmark = landmark,
                visits = state.visits,
                isVisitsLoading = state.isVisitsLoading,
                errorMessage = state.errorMessage,
                onEdit = onEdit,
                onDelete = { viewModel.deleteLandmark(landmark.id) },
                onRefreshVisits = { viewModel.loadVisits(landmark.id) },
                modifier = Modifier.padding(padding)
            )
        }
    }
}