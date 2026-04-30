package com.example.localhistory.ui.landmark

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.ui.components.LocationPicker
import com.example.localhistory.ui.components.imagepicker.LandmarkImagePicker

// A screen available for teacher to create / edit landmarks
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkFormScreen(
    form: LandmarkFormState,
    isSaving: Boolean,
    isEditing: Boolean,
    errorMessage: String? = null,
    errorMessageRes: Int? = null,
    onFormChange: (LandmarkFormState) -> Unit,
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
                            if (isEditing) R.string.landmark_form_edit_title
                            else R.string.landmark_form_add_title
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
                label = { Text(stringResource(R.string.landmark_field_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.description,
                onValueChange = { onFormChange(form.copy(description = it)) },
                label = { Text(stringResource(R.string.landmark_field_description)) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            LandmarkImagePicker(
                imageUrl = form.imageUrl,
                selectedImageUri = form.selectedImageUri,
                onImagePicked = { uri ->
                    onFormChange(form.copy(selectedImageUri = uri))
                }
            )

            LocationPicker(
                latitude = form.latitude,
                longitude = form.longitude,
                onLocationPicked = { latitude, longitude ->
                    onFormChange(
                        form.copy(
                            latitude = latitude.toString(),
                            longitude = longitude.toString()
                        )
                    )
                }
            )

            OutlinedTextField(
                value = form.rewardPoints,
                onValueChange = { onFormChange(form.copy(rewardPoints = it)) },
                label = { Text(stringResource(R.string.landmark_field_reward_points)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

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
