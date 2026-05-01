package com.example.localhistory.ui.homework

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkDTO

// Compact teacher homework card with assignment count and management actions.
@Composable
fun HomeworkCard(
    homework: HomeworkDTO,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = homework.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = homework.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.homework_card_meta,
                        homework.dueDate.toString(),
                        homework.landmarkTitle ?: stringResource(R.string.common_unknown),
                        homework.assignmentsCount
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = homework.requirementsLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                IconButton(onClick = onOpen) {
                    Icon(Icons.Outlined.Visibility, contentDescription = stringResource(R.string.action_view))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }
}

// Builds the short requirement label from the two backend booleans.
@Composable
fun HomeworkDTO.requirementsLabel(): String = when {
    requireVisit && requireQuiz -> stringResource(R.string.homework_requires_visit_and_quiz)
    requireVisit -> stringResource(R.string.homework_requires_visit)
    requireQuiz -> stringResource(R.string.homework_requires_quiz)
    else -> stringResource(R.string.common_unknown)
}
