package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO

// Modern student assignment card with status, requirement progress, and safe landmark navigation.
@Composable
fun StudentHomeworkCard(
    assignment: HomeworkAssignmentDTO,
    isCompleting: Boolean,
    onOpenLandmark: () -> Unit,
    onComplete: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            StudentHomeworkHeader(assignment = assignment)

            Spacer(Modifier.Companion.height(14.dp))

            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Companion.Ellipsis
            )

            Spacer(Modifier.Companion.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.Companion.padding(14.dp)
                ) {
                    HomeworkMetaRow(
                        icon = Icons.Outlined.Schedule,
                        text = stringResource(
                            R.string.homework_due_label,
                            assignment.dueDate.toString()
                        )
                    )
                    HomeworkMetaRow(
                        icon = Icons.Outlined.Place,
                        text = assignment.landmarkTitle ?: stringResource(R.string.common_unknown)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))

                    HomeworkRequirementProgress(assignment)
                }
            }

            Spacer(Modifier.Companion.height(14.dp))

            FilledTonalButton(
                onClick = onOpenLandmark,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Place, contentDescription = null)
                Spacer(Modifier.Companion.width(8.dp))
                Text(
                    stringResource(
                        if (assignment.requireQuiz && !assignment.quizCompleted) {
                            R.string.homework_open_landmark_for_quiz
                        } else {
                            R.string.homework_open_landmark
                        }
                    )
                )
            }

            Spacer(Modifier.Companion.height(8.dp))

            Button(
                onClick = onComplete,
                enabled = assignment.requirementsSatisfied && !assignment.completed && !isCompleting,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                if (isCompleting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.Companion.size(16.dp)
                    )
                } else {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                }
                Spacer(Modifier.Companion.width(8.dp))
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