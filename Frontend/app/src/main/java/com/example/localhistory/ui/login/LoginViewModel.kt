package com.example.localhistory.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.AuthRepository
import com.example.localhistory.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// all possible states the login screen can be in
sealed class LoginState {
    object Idle    : LoginState()   // nothing happening
    object Loading : LoginState()   // API call in flight
    data class Success(val token: String) : LoginState()  // logged in
    data class Error(val message: String) : LoginState()  // something went wrong
}

//TODO: Add better validation and error handling
class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    // StateFlow — the screen observes this and recomposes when it changes
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        // viewModelScope cancels automatically when the ViewModel is destroyed
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    // TODO: save result.data.token to SharedPreferences or DataStore
                    _loginState.value = LoginState.Success(result.data.token.jwtToken)
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }
            }
        }
    }
}