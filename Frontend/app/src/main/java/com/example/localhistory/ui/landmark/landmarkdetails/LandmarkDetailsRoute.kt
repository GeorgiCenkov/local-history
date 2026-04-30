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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R
import com.example.localhistory.ui.components.LoadingContent

// Separate it from the teacher logic to keep it reusable for students
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkDetailRoute(
    landmarkId: Long,
    currentUserId: Long,
    onBack: () -> Unit,
    canSubmitVisit: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onCreateQuiz: ((Long) -> Unit)? = null,
    onEditQuiz: ((Long) -> Unit)? = null,
    onTakeQuiz: ((Long) -> Unit)? = null
) {
    val viewModel: LandmarkDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(landmarkId) {
        viewModel.openLandmarkById(landmarkId)
    }

    LaunchedEffect(landmarkId, onCreateQuiz != null) {
        if (onCreateQuiz != null) {
            viewModel.loadTeacherQuiz(landmarkId)
        }
    }

    LaunchedEffect(landmarkId, canSubmitVisit) {
        if (canSubmitVisit) {
            viewModel.loadPublicQuiz(landmarkId)
        }
    }

    DisposableEffect(lifecycleOwner, landmarkId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Returning from edit should show the saved landmark, not the detail payload
                // that was loaded before navigation.
                viewModel.openLandmarkById(landmarkId)
                if (onCreateQuiz != null) {
                    viewModel.loadTeacherQuiz(landmarkId)
                }
                if (canSubmitVisit) {
                    viewModel.loadPublicQuiz(landmarkId)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                    currentUserId = currentUserId,
                    isVisitsLoading = state.isVisitsLoading,
                    teacherQuiz = state.teacherQuiz,
                    publicQuiz = state.publicQuiz,
                    isQuizLoading = state.isQuizLoading,
                    shouldPromptQuiz = state.shouldPromptQuiz,
                    isSubmittingVisit = state.isSubmittingVisit,
                    errorMessage = state.errorMessage,
                    errorMessageRes = state.errorMessageRes,
                    successMessageRes = state.successMessageRes,
                    onEdit = onEdit,
                    onDelete = null,
                    onRefreshVisits = {
                        viewModel.loadVisits(landmark.id)
                    },
                    onSubmitVisitPhoto = if (canSubmitVisit) {
                        { imageUri -> viewModel.submitVisit(landmark.id, imageUri) }
                    } else {
                        null
                    },
                    onVisitPermissionDenied = if (canSubmitVisit) {
                        viewModel::reportVisitPermissionDenied
                    } else {
                        null
                    },
                    onCreateQuiz = onCreateQuiz?.let {
                        { it(landmark.id) }
                    },
                    onEditQuiz = onEditQuiz,
                    onDeleteQuiz = state.teacherQuiz?.let { quiz ->
                        { viewModel.deleteTeacherQuiz(quiz.id, landmark.id) }
                    },
                    onTakeQuiz = onTakeQuiz,
                    onDismissQuizPrompt = viewModel::dismissQuizPrompt,
                    onDismissVisitMessage = viewModel::dismissVisitMessage,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}
