package com.example.myapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.util.RemoteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading


            when (val result = repository.register(email, pass)) {
                is RemoteResult.Success -> _uiState.value = SignUpUiState.Success
                is RemoteResult.Error -> _uiState.value = SignUpUiState.Error(result.message)
                else -> {}
            }
        }
    }
}


class SignUpViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}