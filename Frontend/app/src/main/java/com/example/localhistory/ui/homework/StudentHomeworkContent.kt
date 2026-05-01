package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.ui.components.LoadingContent

// Student homework list with polished action cards for landmark work and final completion.
@Composable
fun StudentHomeworkContent(
    state: StudentHomeworkUiState,
    onOpenLandmark: (Long) -> Unit,
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
                        onOpenLandmark = { onOpenLandmark(assignment.landmarkId) },
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

// Builds the localized requirement summary for student assignment cards.
@Composable
fun assignmentRequirementsLabel(assignment: HomeworkAssignmentDTO): String = when {
    assignment.requireVisit && assignment.requireQuiz -> stringResource(R.string.homework_requires_visit_and_quiz)
    assignment.requireVisit -> stringResource(R.string.homework_requires_visit)
    assignment.requireQuiz -> stringResource(R.string.homework_requires_quiz)
    else -> stringResource(R.string.common_unknown)
}


