package com.example.localhistory.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.PublicQuizDTO
import com.example.localhistory.model.response.QuizSubmissionResultDTO

// Dialog used by students after they have submitted a visit for the landmark.
@Composable
fun TakeQuizDialog(
    quiz: PublicQuizDTO,
    answers: Map<Long, String>,
    result: QuizSubmissionResultDTO?,
    isSubmitting: Boolean,
    errorMessage: String?,
    errorMessageRes: Int?,
    onAnswerChange: (Long, String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(quiz.title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (result == null) {
                    quiz.questions.forEachIndexed { index, question ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = stringResource(
                                    R.string.quiz_take_question_title,
                                    index + 1,
                                    question.question
                                ),
                                style = MaterialTheme.typography.titleSmall
                            )

                            question.options.forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = answers[question.id] == option,
                                        onClick = { onAnswerChange(question.id, option) }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(
                            R.string.quiz_take_result_summary,
                            result.correctAnswers,
                            result.totalQuestions
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                    result.results.forEachIndexed { index, questionResult ->
                        Text(
                            text = stringResource(
                                if (questionResult.correct) {
                                    R.string.quiz_take_result_correct
                                } else {
                                    R.string.quiz_take_result_incorrect
                                },
                                index + 1,
                                questionResult.correctAnswer
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (questionResult.correct) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }

                val resolvedError = errorMessageRes?.let { stringResource(it) } ?: errorMessage
                if (resolvedError != null) {
                    Text(
                        text = resolvedError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(1.dp))
            }
        },
        confirmButton = {
            if (result == null) {
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(stringResource(R.string.quiz_take_submit))
                    }
                }
            } else {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.action_done))
                }
            }
        },
        dismissButton = {
            if (result == null) {
                TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    )
}
