package com.example.localhistory.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.localhistory.R
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

// Reusable date input that opens a Material date picker instead of relying on manual typing.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null
) {
    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
    val initialSelectedMillis = remember(value) { value.toUtcStartOfDayMillisOrNull() }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedMillis)

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.date_placeholder)) },
        readOnly = true,
        singleLine = true,
        isError = isError,
        supportingText = supportingText,
        trailingIcon = {
            IconButton(onClick = { isDialogOpen = true }) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(R.string.action_select_date)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { isDialogOpen = true }
    )

    if (isDialogOpen) {
        DatePickerDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis
                            ?.toLocalDateString()
                            ?.let(onValueChange)
                        isDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Converts the backend date string into the UTC millis format expected by Material DatePicker.
private fun String.toUtcStartOfDayMillisOrNull(): Long? =
    runCatching {
        LocalDate.parse(this).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }.getOrNull()

// Material DatePicker returns UTC millis, so convert it back to the backend yyyy-MM-dd date string.
private fun Long.toLocalDateString(): String =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
        .toString()
