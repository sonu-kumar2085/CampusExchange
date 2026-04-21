package com.campusexchange.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.repository.AuthRepository
import com.campusexchange.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(emailOrUsername: String, password: String, isEmail: Boolean) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.login(emailOrUsername, password, isEmail)) {
                is Result.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is Result.Error -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun register(fullName: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.register(fullName, email, username, password)) {
                is Result.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is Result.Error -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
