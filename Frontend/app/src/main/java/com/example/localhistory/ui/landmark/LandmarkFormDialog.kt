package com.example.localhistory.ui.landmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.localhistory.R

// Dialog for creating / editing a landmark
@Composable
fun LandmarkFormDialog(
    form: LandmarkFormState,
    isSaving: Boolean,
    onFormChange: (LandmarkFormState) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (form.id == null) R.string.landmark_form_add_title else R.string.landmark_form_edit_title
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { onFormChange(form.copy(title = it)) },
                    label = { Text(stringResource(R.string.landmark_field_title)) },
                    singleLine = true,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.description,
                    onValueChange = { onFormChange(form.copy(description = it)) },
                    label = { Text(stringResource(R.string.landmark_field_description)) },
                    minLines = 2,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.imageUrl,
                    onValueChange = { onFormChange(form.copy(imageUrl = it)) },
                    label = { Text(stringResource(R.string.landmark_field_image_url)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Uri),
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = form.latitude,
                        onValueChange = { onFormChange(form.copy(latitude = it)) },
                        label = { Text(stringResource(R.string.landmark_field_latitude)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Decimal),
                        modifier = Modifier.Companion.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.longitude,
                        onValueChange = { onFormChange(form.copy(longitude = it)) },
                        label = { Text(stringResource(R.string.landmark_field_longitude)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Decimal),
                        modifier = Modifier.Companion.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = form.rewardPoints,
                    onValueChange = { onFormChange(form.copy(rewardPoints = it)) },
                    label = { Text(stringResource(R.string.landmark_field_reward_points)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Number),
                    modifier = Modifier.Companion.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !isSaving) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.Companion.size(16.dp)
                    )
                    Spacer(Modifier.Companion.width(8.dp))
                }
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}