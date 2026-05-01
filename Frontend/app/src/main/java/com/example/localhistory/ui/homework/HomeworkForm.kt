package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.ui.components.DatePickerField

// Form used by teachers to create a landmark-based homework task.
@Composable
fun HomeworkForm(
    state: TeacherHomeworkUiState,
    onFormChange: (HomeworkFormState) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HomeworkMessage(state)

        OutlinedTextField(
            value = state.form.title,
            onValueChange = { onFormChange(state.form.copy(title = it)) },
            label = { Text(stringResource(R.string.homework_field_title)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.form.description,
            onValueChange = { onFormChange(state.form.copy(description = it)) },
            label = { Text(stringResource(R.string.homework_field_description)) },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        DatePickerField(
            value = state.form.dueDate,
            onValueChange = { onFormChange(state.form.copy(dueDate = it)) },
            label = stringResource(R.string.homework_field_due_date),
            modifier = Modifier.fillMaxWidth()
        )

        LandmarkPicker(
            landmarks = state.landmarks,
            selectedLandmarkId = state.form.landmarkId,
            onSelected = { onFormChange(state.form.copy(landmarkId = it)) }
        )

        RequirementCheckbox(
            checked = state.form.requireVisit,
            label = stringResource(R.string.homework_field_require_visit),
            onCheckedChange = { onFormChange(state.form.copy(requireVisit = it)) }
        )

        RequirementCheckbox(
            checked = state.form.requireQuiz,
            label = stringResource(R.string.homework_field_require_quiz),
            onCheckedChange = { onFormChange(state.form.copy(requireQuiz = it)) }
        )

        Text(
            text = stringResource(R.string.homework_quiz_requirement_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onCancel,
                enabled = !state.isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.action_cancel))
            }

            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isSaving) {
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

// Dropdown for choosing which teacher-owned landmark the homework targets.
@Composable
fun LandmarkPicker(
    landmarks: List<LandmarkDTO>,
    selectedLandmarkId: Long?,
    onSelected: (Long) -> Unit
) {
    var expanded by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    val selected = landmarks.firstOrNull { it.id == selectedLandmarkId }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selected?.title ?: stringResource(R.string.homework_field_landmark),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            landmarks.forEach { landmark ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = landmark.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onSelected(landmark.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Row wrapper around a checkbox so requirement options align with the app's form spacing.
@Composable
fun RequirementCheckbox(
    checked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
