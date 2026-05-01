package com.example.localhistory.ui.homework

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.model.response.User

// Detail panel for one homework task, including bulk assignment through student search.
@Composable
fun HomeworkAssignmentContent(
    state: TeacherHomeworkUiState,
    onSearchChange: (String) -> Unit,
    onStudentSelected: (User.Student) -> Unit,
    onStudentRemoved: (Long) -> Unit,
    onAssign: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            HomeworkMessage(state)
            state.selectedHomework?.let { homework ->
                Text(
                    text = stringResource(
                        R.string.homework_detail_meta,
                        homework.dueDate.toString(),
                        homework.landmarkTitle ?: stringResource(R.string.common_unknown)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = homework.requirementsLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            StudentAssignmentPicker(
                state = state,
                onSearchChange = onSearchChange,
                onStudentSelected = onStudentSelected,
                onStudentRemoved = onStudentRemoved,
                onAssign = onAssign
            )
        }

        item {
            Text(
                text = stringResource(R.string.homework_assigned_students, state.assignments.size),
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (state.assignments.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.homework_no_assignments),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(state.assignments, key = { it.id }) { assignment ->
            HomeworkAssignmentCard(assignment = assignment)
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

// Search and selection UI for building a multi-student assignment request.
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentAssignmentPicker(
    state: TeacherHomeworkUiState,
    onSearchChange: (String) -> Unit,
    onStudentSelected: (User.Student) -> Unit,
    onStudentRemoved: (Long) -> Unit,
    onAssign: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = stringResource(R.string.homework_assign_title),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = state.studentSearch,
                onValueChange = onSearchChange,
                label = { Text(stringResource(R.string.homework_student_search)) },
                singleLine = true,
                trailingIcon = {
                    if (state.isSearching) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            state.searchResults.take(5).forEach { student ->
                StudentSearchRow(
                    student = student,
                    onClick = { onStudentSelected(student) }
                )
            }

            if (state.selectedStudents.isNotEmpty()) {
                // FlowRow keeps selected students compact while wrapping safely on narrow screens.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.selectedStudents.forEach { student ->
                        AssistChip(
                            onClick = { onStudentRemoved(student.id) },
                            label = { Text(student.fullName()) },
                            trailingIcon = {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.homework_remove_student),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onAssign,
                enabled = !state.isAssigning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isAssigning) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                } else {
                    Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                }
                Text(stringResource(R.string.homework_assign_action))
            }
        }
    }
}

// Search result row for one student returned by the backend search endpoint.
@Composable
fun StudentSearchRow(
    student: User.Student,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.fullName(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = student.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(Icons.Outlined.PersonAdd, contentDescription = null)
    }
}

// Read-only card showing one student's assigned homework status.
@Composable
fun HomeworkAssignmentCard(assignment: HomeworkAssignmentDTO) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = if (assignment.completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.student?.fullName() ?: stringResource(R.string.common_unknown),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = assignment.student?.email.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
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
        }
    }
}

// Formats a user name consistently across search results, chips, and assignment cards.
fun User.fullName(): String = "$firstName $lastName".trim()
