package com.example.myapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.util.RemoteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState


    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            when (val result = repository.login(email, pass)) {
                is RemoteResult.Success -> _uiState.value = LoginUiState.Success
                is RemoteResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                else -> {}
            }
        }
    }


    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            when (val result = repository.register(email, pass)) {
                is RemoteResult.Success -> _uiState.value = LoginUiState.Success
                is RemoteResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                else -> {}
            }
        }
    }
}


class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}