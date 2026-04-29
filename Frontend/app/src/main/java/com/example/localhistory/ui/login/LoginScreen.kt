package com.example.localhistory.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showPassword: Boolean = false,
    // per-field validation errors; null means no error shown yet
    val emailError: String? = null,
    val passwordError: String? = null
)

/** Returns null if valid, else a resource string key describing the problem. */
private fun validateEmail(email: String): Int? = when {
    email.isBlank() -> R.string.validation_email_empty
    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.validation_email_invalid
    else -> null
}

private fun validatePassword(password: String): Int? = when {
    password.isBlank() -> R.string.validation_password_empty
    password.length < 8 -> R.string.validation_password_too_short
    else -> null
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    var state by remember { mutableStateOf(LoginUiState()) }

    // Resolve validation strings up-front (must be inside @Composable scope)
    val emailEmptyError     = stringResource(R.string.validation_email_empty)
    val emailInvalidError   = stringResource(R.string.validation_email_invalid)
    val passwordEmptyError  = stringResource(R.string.validation_password_empty)
    val passwordShortError  = stringResource(R.string.validation_password_too_short)

    fun resolveEmailError(email: String): String? = when (validateEmail(email)) {
        R.string.validation_email_empty   -> emailEmptyError
        R.string.validation_email_invalid -> emailInvalidError
        else -> null
    }

    fun resolvePasswordError(password: String): String? = when (validatePassword(password)) {
        R.string.validation_password_empty     -> passwordEmptyError
        R.string.validation_password_too_short -> passwordShortError
        else -> null
    }

    // Sync loading/error state from ViewModel back into UI state
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Loading -> {
                state = state.copy(isLoading = true, errorMessage = null)
            }

            is LoginState.Error -> {
                state = state.copy(
                    isLoading = false,
                    errorMessage = (loginState as LoginState.Error).message
                )
            }

            is LoginState.Success -> {
                state = state.copy(isLoading = false)
                onLoginSuccess()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.login_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ── Email ────────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.email,
            onValueChange = {
                // Clear field error while the user is typing; re-validate on focus-loss
                // is handled implicitly when they press the login button.
                state = state.copy(email = it, emailError = null)
            },
            label = { Text(stringResource(R.string.login_email)) },
            singleLine = true,
            isError = state.emailError != null,
            supportingText = state.emailError?.let { msg ->
                { Text(msg, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Password ─────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.password,
            onValueChange = { state = state.copy(password = it, passwordError = null) },
            label = { Text(stringResource(R.string.login_password)) },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { msg ->
                { Text(msg, color = MaterialTheme.colorScheme.error) }
            },
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { state = state.copy(showPassword = !state.showPassword) }) {
                    Icon(
                        imageVector = if (state.showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = stringResource(
                            if (state.showPassword) R.string.login_hide_password else R.string.login_show_password
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Global error (e.g. wrong credentials from server)
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validate all fields; collect errors before early-returning so every
                // field shows its error at the same time rather than one-by-one.
                val emailErr    = resolveEmailError(state.email)
                val passwordErr = resolvePasswordError(state.password)

                if (emailErr != null || passwordErr != null) {
                    state = state.copy(emailError = emailErr, passwordError = passwordErr)
                    return@Button
                }

                viewModel.login(state.email, state.password)
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
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(stringResource(if (state.isLoading) R.string.login_button_loading else R.string.login_button))
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onRegisterClick) {
            Text(stringResource(R.string.login_register_prompt))
        }
    }
}