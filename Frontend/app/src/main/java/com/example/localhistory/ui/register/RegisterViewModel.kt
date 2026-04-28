package com.example.localhistory.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.repository.AuthRepository
import com.example.localhistory.data.repository.AuthResult
import com.example.localhistory.model.response.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        birthDate: LocalDate,
        role: Role
    ) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            when (val result = repository.register(firstName, lastName, email, password, birthDate, role)) {
                is AuthResult.Success -> _state.value = RegisterState.Success(result.data.token.jwtToken)
                is AuthResult.Error -> _state.value = RegisterState.Error(result.message)
            }
        }
    }
}