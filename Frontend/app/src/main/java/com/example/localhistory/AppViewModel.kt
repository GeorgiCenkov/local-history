package com.example.localhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localhistory.data.datastore.AuthDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authDataStore: AuthDataStore
) : ViewModel() {

    val accessToken: StateFlow<String?> = authDataStore.accessToken
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LOADING")
}