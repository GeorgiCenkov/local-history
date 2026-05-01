package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO

// Rounded status badge that is easier to scan than plain text.
@Composable
fun HomeworkStatusPill(assignment: HomeworkAssignmentDTO) {
    val status = assignment.statusVisuals()
    Surface(
        color = status.containerColor,
        contentColor = status.contentColor,
        shape = CircleShape
    ) {
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.Companion.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(status.icon, contentDescription = null, modifier = Modifier.Companion.size(16.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}


// Keeps status color/icon decisions together so the card stays easy to scan.
@Composable
private fun HomeworkAssignmentDTO.statusVisuals(): HomeworkStatusVisuals = when {
    completed -> HomeworkStatusVisuals(
        label = stringResource(R.string.homework_status_completed),
        icon = Icons.Outlined.CheckCircle,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    requirementsSatisfied -> HomeworkStatusVisuals(
        label = stringResource(R.string.homework_status_ready),
        icon = Icons.Outlined.Flag,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    overdue -> HomeworkStatusVisuals(
        label = stringResource(R.string.homework_status_overdue),
        icon = Icons.Outlined.Schedule,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
    else -> HomeworkStatusVisuals(
        label = stringResource(R.string.homework_status_pending),
        icon = Icons.Outlined.Schedule,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private data class HomeworkStatusVisuals(
    val label: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)
