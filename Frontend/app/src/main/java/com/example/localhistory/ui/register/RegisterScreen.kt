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

// all form fields in one place — easy to validate in one shot
data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: String = "",        // stored as string, parsed to LocalDate on submit
    val role: Role = Role.STUDENT,
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val registerState by viewModel.state.collectAsStateWithLifecycle()
    var state by remember { mutableStateOf(RegisterUiState()) }

    // resolve validation strings before composing so they're available in onClick
    val errorEmpty = stringResource(R.string.register_error_empty_fields)
    val errorPassword = stringResource(R.string.register_error_password_mismatch)
    val errorDate = stringResource(R.string.register_error_invalid_date)

    // react to ViewModel state changes
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Loading -> state = state.copy(isLoading = true, errorMessage = null)
            is RegisterState.Error -> state = state.copy(isLoading = false, errorMessage = (registerState as RegisterState.Error).message)
            is RegisterState.Success -> onRegisterSuccess()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // scroll for smaller screens
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

        // role toggle — student or teacher
        RoleToggle(
            selected = state.role,
            onRoleSelected = { state = state.copy(role = it) }
        )

        Spacer(Modifier.height(24.dp))

        // name row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { state = state.copy(firstName = it) },
                label = { Text(stringResource(R.string.register_first_name)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { state = state.copy(lastName = it) },
                label = { Text(stringResource(R.string.register_last_name)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { state = state.copy(email = it) },
            label = { Text(stringResource(R.string.register_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // date hint: YYYY-MM-DD
        OutlinedTextField(
            value = state.birthDate,
            onValueChange = { state = state.copy(birthDate = it) },
            label = { Text(stringResource(R.string.register_birth_date)) },
            placeholder = { Text("YYYY-MM-DD") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { state = state.copy(password = it) },
            label = { Text(stringResource(R.string.register_password)) },
            singleLine = true,
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

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { state = state.copy(confirmPassword = it) },
            label = { Text(stringResource(R.string.register_confirm_password)) },
            singleLine = true,
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // validate before hitting the network
                if (state.firstName.isBlank() || state.lastName.isBlank() ||
                    state.email.isBlank() || state.password.isBlank() || state.birthDate.isBlank()) {
                    state = state.copy(errorMessage = errorEmpty)
                    return@Button
                }
                if (state.password != state.confirmPassword) {
                    state = state.copy(errorMessage = errorPassword)
                    return@Button
                }
                // parse date — show error if format is wrong
                val parsedDate = try {
                    LocalDate.parse(state.birthDate)
                } catch (e: Exception) {
                    state = state.copy(errorMessage = errorDate)
                    return@Button
                }
                viewModel.register(
                    state.firstName, state.lastName, state.email,
                    state.password, parsedDate, state.role
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
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

// animated toggle between Student and Teacher
@Composable
fun RoleToggle(selected: Role, onRoleSelected: (Role) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
    ) {
        Role.entries.forEach { role ->
            val isSelected = selected == role
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                animationSpec = tween(200)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(200)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(bgColor)
                    .clickable { onRoleSelected(role) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(if (role == Role.STUDENT) R.string.register_role_student else R.string.register_role_teacher),
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}