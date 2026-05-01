package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.model.response.HomeworkAssignmentDTO

// Header for a student card: title on the left, state badge on the right.
@Composable
fun StudentHomeworkHeader(assignment: HomeworkAssignmentDTO) {
    Row(verticalAlignment = Alignment.Companion.Top) {
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Companion.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Companion.Ellipsis
            )
            Spacer(Modifier.Companion.height(4.dp))
            Text(
                text = assignmentRequirementsLabel(assignment),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HomeworkStatusPill(assignment)
    }
}