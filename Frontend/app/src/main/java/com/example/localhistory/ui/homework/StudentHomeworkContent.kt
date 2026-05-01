package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.ui.components.LoadingContent

// Student homework list with action cards for visits, quizzes, and final completion.
@Composable
fun StudentHomeworkContent(
    state: StudentHomeworkUiState,
    onOpenLandmark: (Long) -> Unit,
    onTakeQuiz: (HomeworkAssignmentDTO) -> Unit,
    onComplete: (HomeworkAssignmentDTO) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        StudentHomeworkMessage(state)

        when {
            state.isLoading && state.assignments.isEmpty() -> LoadingContent()
            state.assignments.isEmpty() -> EmptyHomework(
                title = stringResource(R.string.homework_student_empty_title),
                body = stringResource(R.string.homework_student_empty_body),
                actionText = null,
                onAction = null
            )
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.homework_student_subtitle, state.assignments.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                items(state.assignments, key = { it.id }) { assignment ->
                    StudentHomeworkCard(
                        assignment = assignment,
                        isCompleting = state.completingAssignmentId == assignment.id,
                        isLoadingQuiz = state.loadingQuizAssignmentId == assignment.id,
                        onOpenLandmark = { onOpenLandmark(assignment.landmarkId) },
                        onTakeQuiz = { onTakeQuiz(assignment) },
                        onComplete = { onComplete(assignment) }
                    )
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

// Compact message row for student homework errors and successful completion.
@Composable
fun StudentHomeworkMessage(state: StudentHomeworkUiState) {
    val error = state.errorMessageRes?.let { stringResource(it) } ?: state.errorMessage
    val success = state.successMessageRes?.let { stringResource(it) }
    val message = error ?: success ?: return

    Text(
        text = message,
        color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// Student-facing assignment card with only the actions that still make sense for its state.
@Composable
fun StudentHomeworkCard(
    assignment: HomeworkAssignmentDTO,
    isCompleting: Boolean,
    isLoadingQuiz: Boolean,
    onOpenLandmark: () -> Unit,
    onTakeQuiz: () -> Unit,
    onComplete: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                HomeworkStatusLabel(assignment)
            }

            Text(
                text = stringResource(
                    R.string.homework_detail_meta,
                    assignment.dueDate.toString(),
                    assignment.landmarkTitle ?: stringResource(R.string.common_unknown)
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = assignmentRequirementsLabel(assignment),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HomeworkRequirementProgress(assignment)

            FilledTonalButton(
                onClick = onOpenLandmark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Place, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.homework_open_landmark))
            }

            if (assignment.requireQuiz && !assignment.quizCompleted) {
                FilledTonalButton(
                    onClick = onTakeQuiz,
                    enabled = !isLoadingQuiz,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoadingQuiz) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(Icons.Outlined.Quiz, contentDescription = null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.homework_take_quiz))
                }
            }

            Button(
                onClick = onComplete,
                enabled = assignment.requirementsSatisfied && !assignment.completed && !isCompleting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isCompleting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(
                        if (assignment.completed) {
                            R.string.homework_status_completed
                        } else {
                            R.string.homework_mark_complete
                        }
                    )
                )
            }
        }
    }
}

// Status text for the top-right corner of the student assignment card.
@Composable
fun HomeworkStatusLabel(assignment: HomeworkAssignmentDTO) {
    Text(
        text = when {
            assignment.completed -> stringResource(R.string.homework_status_completed)
            assignment.requirementsSatisfied -> stringResource(R.string.homework_status_ready)
            assignment.overdue -> stringResource(R.string.homework_status_overdue)
            else -> stringResource(R.string.homework_status_pending)
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

// Shows which homework requirements are already satisfied for the student.
@Composable
fun HomeworkRequirementProgress(assignment: HomeworkAssignmentDTO) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (assignment.requireVisit) {
            RequirementProgressLine(
                done = assignment.visitCompleted,
                text = stringResource(R.string.homework_visit_progress)
            )
        }
        if (assignment.requireQuiz) {
            RequirementProgressLine(
                done = assignment.quizCompleted,
                text = stringResource(R.string.homework_quiz_progress)
            )
        }
    }
}

// One row inside the assignment requirement checklist.
@Composable
fun RequirementProgressLine(
    done: Boolean,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Builds the localized requirement summary for student assignment cards.
@Composable
fun assignmentRequirementsLabel(assignment: HomeworkAssignmentDTO): String = when {
    assignment.requireVisit && assignment.requireQuiz -> stringResource(R.string.homework_requires_visit_and_quiz)
    assignment.requireVisit -> stringResource(R.string.homework_requires_visit)
    assignment.requireQuiz -> stringResource(R.string.homework_requires_quiz)
    else -> stringResource(R.string.common_unknown)
}
