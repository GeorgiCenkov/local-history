package com.example.localhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.datastore.AuthSession
import com.example.localhistory.data.datastore.AuthDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authDataStore: AuthDataStore
) : ViewModel() {

    val authSession: StateFlow<AuthSession?> = authDataStore.authSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // True once the datastore has emitted at least one real value
    val isSessionLoaded: StateFlow<Boolean> = authDataStore.authSession
        .map { true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun logout() {
        viewModelScope.launch {
            authDataStore.clear()
        }
    }
}
