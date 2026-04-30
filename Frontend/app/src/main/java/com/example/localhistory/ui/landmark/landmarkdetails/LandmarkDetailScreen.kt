package com.example.localhistory.ui.landmark.landmarkdetails

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
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.model.response.LandmarkVisitDTO
import com.example.localhistory.model.response.PublicQuizDTO
import com.example.localhistory.model.response.QuizDTO
import com.example.localhistory.model.response.QuizSubmissionResultDTO
import com.example.localhistory.ui.components.LoadingContent
import com.example.localhistory.ui.components.NetworkImage
import com.example.localhistory.ui.quiz.TakeQuizDialog
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

// Detailed view of a single landmark
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkDetailScreen(
    landmark: LandmarkDTO,
    modifier: Modifier = Modifier,
    visits: List<LandmarkVisitDTO> = emptyList(),
    currentUserId: Long? = null,
    teacherQuiz: QuizDTO? = null,
    publicQuiz: PublicQuizDTO? = null,
    quizAnswers: Map<Long, String> = emptyMap(),
    quizSubmissionResult: QuizSubmissionResultDTO? = null,
    isVisitsLoading: Boolean = false,
    isQuizLoading: Boolean = false,
    isQuizSubmitting: Boolean = false,
    isQuizDialogVisible: Boolean = false,
    shouldPromptQuiz: Boolean = false,
    isSubmittingVisit: Boolean = false,
    errorMessage: String? = null,
    errorMessageRes: Int? = null,
    successMessageRes: Int? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRefreshVisits: (() -> Unit)? = null,
    onCreateQuiz: (() -> Unit)? = null,
    onEditQuiz: ((Long) -> Unit)? = null,
    onDeleteQuiz: (() -> Unit)? = null,
    onTakeQuiz: (() -> Unit)? = null,
    onDismissQuizPrompt: () -> Unit = {},
    onDismissQuizDialog: () -> Unit = {},
    onQuizAnswerChange: (Long, String) -> Unit = { _, _ -> },
    onSubmitQuiz: () -> Unit = {},
    onSubmitVisitPhoto: ((String) -> Unit)? = null,
    onVisitPermissionDenied: (() -> Unit)? = null,
    onDismissVisitMessage: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isMapSheetVisible by remember { mutableStateOf(false) }
    val hasSubmittedVisit = currentUserId != null && visits.any { it.userId == currentUserId }
    val canTakePublicQuiz = onTakeQuiz != null && hasSubmittedVisit && publicQuiz != null

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (successMessageRes != null) {
            item {
                Text(
                    text = stringResource(successMessageRes),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    NetworkImage(
                        imageUrl = landmark.imageUrl,
                        contentDescription = landmark.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )

                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = landmark.title,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = landmark.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(14.dp))

                        LandmarkStats(landmark)

                        Spacer(Modifier.height(14.dp))

                        FilledTonalButton(
                            onClick = { isMapSheetVisible = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.Place, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.landmark_action_show_map))
                        }

                        if (onEdit != null || onDelete != null) {
                            Spacer(Modifier.height(14.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (onEdit != null) {
                                    FilledTonalButton(onClick = onEdit) {
                                        Icon(Icons.Outlined.Edit, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.action_edit))
                                    }
                                }

                                if (onDelete != null) {
                                    TextButton(onClick = onDelete) {
                                        Icon(Icons.Outlined.Delete, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.action_delete))
                                    }
                                }
                            }
                        }

                        if (onSubmitVisitPhoto != null && onVisitPermissionDenied != null) {
                            VisitPhotoCaptureButton(
                                isSubmitting = isSubmittingVisit,
                                onPhotoCaptured = onSubmitVisitPhoto,
                                onPermissionDenied = onVisitPermissionDenied,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (canTakePublicQuiz) {
                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = { onTakeQuiz?.invoke() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.Quiz, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.quiz_take_action))
                            }
                        }
                    }
                }
            }
        }

        // If a teacher is accessing this, show the quiz management section
        if (onCreateQuiz != null) {
            item {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Quiz, contentDescription = null)
                            Text(
                                text = stringResource(R.string.quiz_section_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        when {
                            isQuizLoading -> CircularProgressIndicator()

                            teacherQuiz == null -> {
                                Text(
                                    text = stringResource(R.string.quiz_empty_for_landmark),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                FilledTonalButton(onClick = onCreateQuiz) {
                                    Text(stringResource(R.string.quiz_action_create))
                                }
                            }

                            else -> {
                                Text(
                                    text = teacherQuiz.title,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Text(
                                    text = stringResource(
                                        R.string.quiz_question_count,
                                        teacherQuiz.questions.size
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    FilledTonalButton(
                                        onClick = { onEditQuiz?.invoke(teacherQuiz.id) }
                                    ) {
                                        Icon(Icons.Outlined.Edit, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.action_edit))
                                    }

                                    TextButton(onClick = { onDeleteQuiz?.invoke() }) {
                                        Icon(Icons.Outlined.Delete, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.action_delete))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.landmark_visits_title),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = stringResource(R.string.landmark_visits_subtitle, visits.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onRefreshVisits != null) {
                    IconButton(onClick = onRefreshVisits) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.action_refresh)
                        )
                    }
                }
            }
        }

        when {
            isVisitsLoading && visits.isEmpty() -> item {
                LoadingContent(Modifier.height(160.dp))
            }

            visits.isEmpty() -> item {
                EmptyVisits()
            }

            else -> items(visits, key = { it.id }) { visit ->
                VisitCard(visit = visit)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
        }
    }

    // Hide-able bottom sheet for the landmark coordinates
    if (isMapSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isMapSheetVisible = false },
            sheetState = sheetState
        ) {
            LandmarkMapSheetContent(
                landmark = landmark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onOpenDirections = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        isMapSheetVisible = false
                    }
                }
            )
        }
    }

    if (errorMessageRes != null && !isQuizDialogVisible) {
        AlertDialog(
            onDismissRequest = onDismissVisitMessage,
            title = {
                Text(stringResource(R.string.landmark_visit_error_title))
            },
            text = {
                Text(stringResource(errorMessageRes))
            },
            confirmButton = {
                TextButton(onClick = onDismissVisitMessage) {
                    Text(stringResource(R.string.action_ok))
                }
            }
        )
    }

    if (shouldPromptQuiz && canTakePublicQuiz) {
        AlertDialog(
            onDismissRequest = onDismissQuizPrompt,
            title = {
                Text(stringResource(R.string.quiz_prompt_title))
            },
            text = {
                Text(stringResource(R.string.quiz_prompt_body))
            },
            confirmButton = {
                Button(onClick = { onTakeQuiz?.invoke() }) {
                    Text(stringResource(R.string.quiz_take_action))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissQuizPrompt) {
                    Text(stringResource(R.string.action_later))
                }
            }
        )
    }

    if (isQuizDialogVisible && publicQuiz != null) {
        TakeQuizDialog(
            quiz = publicQuiz,
            answers = quizAnswers,
            result = quizSubmissionResult,
            isSubmitting = isQuizSubmitting,
            errorMessage = errorMessage,
            errorMessageRes = errorMessageRes,
            onAnswerChange = onQuizAnswerChange,
            onSubmit = onSubmitQuiz,
            onDismiss = onDismissQuizDialog
        )
    }
}
