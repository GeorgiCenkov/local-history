package com.example.localhistory.ui.register

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.localhistory.R
import com.example.localhistory.model.response.Role
import kotlinx.datetime.LocalDate

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: String = "",
    val role: Role = Role.STUDENT,
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // per-field validation errors
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val birthDateError: String? = null
)

// ── Pure validation helpers (return string-resource IDs so they stay testable) ──

private fun validateName(value: String): Int? =
    if (value.isBlank()) R.string.validation_name_empty else null

private fun validateEmail(email: String): Int? = when {
    email.isBlank() -> R.string.validation_email_empty
    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.validation_email_invalid
    else -> null
}

private fun validatePassword(password: String): Int? = when {
    password.isBlank() -> R.string.validation_password_empty
    password.length < 8 -> R.string.validation_password_too_short
    !password.any { it.isUpperCase() } -> R.string.validation_password_no_uppercase
    !password.any { it.isDigit() } -> R.string.validation_password_no_digit
    else -> null
}

private fun validateConfirmPassword(password: String, confirm: String): Int? = when {
    confirm.isBlank() -> R.string.validation_password_empty
    password != confirm -> R.string.validation_password_mismatch
    else -> null
}

private fun validateBirthDate(raw: String): Int? = when {
    raw.isBlank() -> R.string.validation_date_empty
    else -> try {
        LocalDate.parse(raw)
        null // valid
    } catch (_: Exception) {
        R.string.validation_date_invalid
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val registerState by viewModel.state.collectAsStateWithLifecycle()
    var state by remember { mutableStateOf(RegisterUiState()) }

    // ── Resolve all validation strings inside @Composable scope ──────────────
    val errNameEmpty        = stringResource(R.string.validation_name_empty)
    val errEmailEmpty       = stringResource(R.string.validation_email_empty)
    val errEmailInvalid     = stringResource(R.string.validation_email_invalid)
    val errPasswordEmpty    = stringResource(R.string.validation_password_empty)
    val errPasswordShort    = stringResource(R.string.validation_password_too_short)
    val errPasswordUpper    = stringResource(R.string.validation_password_no_uppercase)
    val errPasswordDigit    = stringResource(R.string.validation_password_no_digit)
    val errPasswordMismatch = stringResource(R.string.validation_password_mismatch)
    val errDateEmpty        = stringResource(R.string.validation_date_empty)
    val errDateInvalid      = stringResource(R.string.validation_date_invalid)

    // Helper: map resource ID -> resolved string (avoids repeating the when-chain)
    fun resName(id: Int?): String? = when (id) {
        R.string.validation_name_empty -> errNameEmpty
        else -> null
    }

    fun resEmail(id: Int?): String? = when (id) {
        R.string.validation_email_empty   -> errEmailEmpty
        R.string.validation_email_invalid -> errEmailInvalid
        else -> null
    }

    fun resPassword(id: Int?): String? = when (id) {
        R.string.validation_password_empty       -> errPasswordEmpty
        R.string.validation_password_too_short   -> errPasswordShort
        R.string.validation_password_no_uppercase -> errPasswordUpper
        R.string.validation_password_no_digit    -> errPasswordDigit
        else -> null
    }

    fun resConfirm(id: Int?): String? = when (id) {
        R.string.validation_password_empty    -> errPasswordEmpty
        R.string.validation_password_mismatch -> errPasswordMismatch
        else -> null
    }

    fun resDate(id: Int?): String? = when (id) {
        R.string.validation_date_empty   -> errDateEmpty
        R.string.validation_date_invalid -> errDateInvalid
        else -> null
    }

    // React to ViewModel state changes
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Loading -> state = state.copy(isLoading = true, errorMessage = null)
            is RegisterState.Error   -> state = state.copy(isLoading = false, errorMessage = (registerState as RegisterState.Error).message)
            is RegisterState.Success -> onRegisterSuccess()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.register_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(32.dp))

        RoleToggle(
            selected = state.role,
            onRoleSelected = { state = state.copy(role = it) }
        )

        Spacer(Modifier.height(24.dp))

        // ── Name row ──────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { state = state.copy(firstName = it, firstNameError = null) },
                label = { Text(stringResource(R.string.register_first_name)) },
                singleLine = true,
                isError = state.firstNameError != null,
                supportingText = state.firstNameError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { state = state.copy(lastName = it, lastNameError = null) },
                label = { Text(stringResource(R.string.register_last_name)) },
                singleLine = true,
                isError = state.lastNameError != null,
                supportingText = state.lastNameError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Email ─────────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.email,
            onValueChange = { state = state.copy(email = it, emailError = null) },
            label = { Text(stringResource(R.string.register_email)) },
            singleLine = true,
            isError = state.emailError != null,
            supportingText = state.emailError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Birth date ────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.birthDate,
            onValueChange = { state = state.copy(birthDate = it, birthDateError = null) },
            label = { Text(stringResource(R.string.register_birth_date)) },
            placeholder = { Text("YYYY-MM-DD") },
            singleLine = true,
            isError = state.birthDateError != null,
            supportingText = state.birthDateError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Password ──────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.password,
            onValueChange = { state = state.copy(password = it, passwordError = null) },
            label = { Text(stringResource(R.string.register_password)) },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { state = state.copy(showPassword = !state.showPassword) }) {
                    Icon(
                        imageVector = if (state.showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Confirm password ──────────────────────────────────────────────────
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { state = state.copy(confirmPassword = it, confirmPasswordError = null) },
            label = { Text(stringResource(R.string.register_confirm_password)) },
            singleLine = true,
            isError = state.confirmPasswordError != null,
            supportingText = state.confirmPasswordError?.let { msg -> { Text(msg, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // Global server-side error
        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // Validate everything at once so all errors show simultaneously
                val firstNameErr       = resName(validateName(state.firstName))
                val lastNameErr        = resName(validateName(state.lastName))
                val emailErr           = resEmail(validateEmail(state.email))
                val passwordErr        = resPassword(validatePassword(state.password))
                val confirmPasswordErr = resConfirm(validateConfirmPassword(state.password, state.confirmPassword))
                val birthDateErr       = resDate(validateBirthDate(state.birthDate))

                if (listOf(firstNameErr, lastNameErr, emailErr, passwordErr, confirmPasswordErr, birthDateErr).any { it != null }) {
                    state = state.copy(
                        firstNameError       = firstNameErr,
                        lastNameError        = lastNameErr,
                        emailError           = emailErr,
                        passwordError        = passwordErr,
                        confirmPasswordError = confirmPasswordErr,
                        birthDateError       = birthDateErr
                    )
                    return@Button
                }

                // Safe to parse — already validated above
                val parsedDate = LocalDate.parse(state.birthDate)
                viewModel.register(
                    state.firstName, state.lastName, state.email,
                    state.password, parsedDate, state.role
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(stringResource(if (state.isLoading) R.string.register_button_loading else R.string.register_button))
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBackToLogin) {
            Text(stringResource(R.string.register_back_to_login))
        }
    }
}