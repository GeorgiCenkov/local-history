package com.example.localhistory.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizFormScreen(
    form: QuizFormState,
    isLoading: Boolean,
    isSaving: Boolean,
    isEditing: Boolean,
    errorMessage: String? = null,
    errorMessageRes: Int? = null,
    onFormChange: (QuizFormState) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isEditing) R.string.quiz_form_edit_title
                            else R.string.quiz_form_create_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSaving) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
            return@Scaffold
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = form.title,
                onValueChange = { onFormChange(form.copy(title = it)) },
                label = { Text(stringResource(R.string.quiz_field_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            form.questions.forEachIndexed { questionIndex, question ->
                QuizQuestionEditor(
                    questionNumber = questionIndex + 1,
                    question = question,
                    canDelete = form.questions.size > 1,
                    onQuestionChange = { updatedQuestion ->
                        onFormChange(
                            form.copy(
                                questions = form.questions.mapIndexed { index, current ->
                                    if (index == questionIndex) updatedQuestion else current
                                }
                            )
                        )
                    },
                    onDelete = {
                        onFormChange(
                            form.copy(
                                questions = form.questions.filterIndexed { index, _ ->
                                    index != questionIndex
                                }
                            )
                        )
                    }
                )
            }

            TextButton(
                onClick = {
                    onFormChange(
                        form.copy(questions = form.questions + QuizQuestionFormState())
                    )
                }
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.quiz_action_add_question))
            }

            val resolvedErrorMessage = errorMessageRes?.let { stringResource(it) } ?: errorMessage
            if (resolvedErrorMessage != null) {
                Text(
                    text = resolvedErrorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onBack,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }

                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}

@Composable
private fun QuizQuestionEditor(
    questionNumber: Int,
    question: QuizQuestionFormState,
    canDelete: Boolean,
    onQuestionChange: (QuizQuestionFormState) -> Unit,
    onDelete: () -> Unit
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.quiz_question_label, questionNumber),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )

                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.quiz_action_delete_question)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = question.question,
                onValueChange = { onQuestionChange(question.copy(question = it)) },
                label = { Text(stringResource(R.string.quiz_field_question)) },
                modifier = Modifier.fillMaxWidth()
            )

            question.options.forEachIndexed { optionIndex, option ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = option,
                        onValueChange = { updated ->
                            onQuestionChange(
                                question.copy(
                                    options = question.options.mapIndexed { index, current ->
                                        if (index == optionIndex) updated else current
                                    }
                                )
                            )
                        },
                        label = {
                            Text(stringResource(R.string.quiz_field_option, optionIndex + 1))
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    if (question.options.size > 2) {
                        IconButton(
                            onClick = {
                                onQuestionChange(
                                    question.copy(
                                        options = question.options.filterIndexed { index, _ ->
                                            index != optionIndex
                                        }
                                    )
                                )
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.quiz_action_delete_option)
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = { onQuestionChange(question.copy(options = question.options + "")) }
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.quiz_action_add_option))
            }

            OutlinedTextField(
                value = question.correctAnswer,
                onValueChange = { onQuestionChange(question.copy(correctAnswer = it)) },
                label = { Text(stringResource(R.string.quiz_field_correct_answer)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
