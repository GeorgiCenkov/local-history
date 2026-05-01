package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkAssignmentDTO

// Shows which homework requirements are already satisfied for the student.
@Composable
fun HomeworkRequirementProgress(assignment: HomeworkAssignmentDTO) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (assignment.requireVisit) {
            RequirementProgressLine(
                done = assignment.visitCompleted,
                icon = Icons.Outlined.Place,
                text = stringResource(R.string.homework_visit_progress)
            )
        }
        if (assignment.requireQuiz) {
            RequirementProgressLine(
                done = assignment.quizCompleted,
                icon = Icons.Outlined.Quiz,
                text = stringResource(R.string.homework_quiz_progress)
            )
        }
    }
}