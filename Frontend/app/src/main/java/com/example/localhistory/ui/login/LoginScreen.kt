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
import com.example.localhistory.R

// UI state — keeps all login screen state in one place
data class LoginUiState(
    val email:        String  = "",
    val password:     String  = "",
    val isLoading:    Boolean = false,
    val errorMessage: String? = null,
    val showPassword: Boolean = false
)

@Composable
fun LoginScreen(
    // called when the user taps login — wire your API call here
    onLoginClick:    (email: String, password: String) -> Unit,
    // called when the user taps "don't have an account"
    onRegisterClick: () -> Unit
) {
    var state by remember { mutableStateOf(LoginUiState()) }

    // resolve strings once so we can use them in both UI and logic
    val errorEmptyFields = stringResource(R.string.login_error_empty_fields)

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text  = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text  = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // email field
        OutlinedTextField(
            value           = state.email,
            onValueChange   = { state = state.copy(email = it) },
            label           = { Text(stringResource(R.string.login_email)) },
            singleLine      = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // password field with show/hide toggle
        OutlinedTextField(
            value                = state.password,
            onValueChange        = { state = state.copy(password = it) },
            label                = { Text(stringResource(R.string.login_password)) },
            singleLine           = true,
            visualTransformation = if (state.showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon    = {
                IconButton(onClick = { state = state.copy(showPassword = !state.showPassword) }) {
                    Icon(
                        imageVector        = if (state.showPassword)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = stringResource(
                            if (state.showPassword) R.string.login_hide_password
                            else R.string.login_show_password
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // only visible when there's an error
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // login button — disabled and shows spinner while API call is in flight
        Button(
            onClick = {
                if (state.email.isBlank() || state.password.isBlank()) {
                    state = state.copy(errorMessage = errorEmptyFields)
                    return@Button
                }
                state = state.copy(isLoading = true, errorMessage = null)
                onLoginClick(state.email, state.password)
            },
            enabled  = !state.isLoading,
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
            Text(
                stringResource(
                    if (state.isLoading) R.string.login_button_loading
                    else R.string.login_button
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onRegisterClick) {
            Text(stringResource(R.string.login_register_prompt))
        }
    }
}