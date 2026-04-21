package com.campusexchange.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.remote.dto.StepsDto
import com.campusexchange.app.data.remote.dto.UserDto
import com.campusexchange.app.data.remote.dto.WalletDto
import com.campusexchange.app.data.repository.AuthRepository
import com.campusexchange.app.data.repository.Result
import com.campusexchange.app.data.repository.StepRepository
import com.campusexchange.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val wallet: WalletDto? = null,
    val remoteSteps: StepsDto? = null,
    val bio: String = "",
    val error: String? = null,
    val changePasswordSuccess: Boolean = false,
    val changePasswordError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userResult   = userRepository.getCurrentUser()
            val walletResult = userRepository.getWallet()
            val stepsResult  = stepRepository.getRemoteSteps()

            _uiState.update {
                it.copy(
                    isLoading   = false,
                    user        = if (userResult   is Result.Success) userResult.data   else null,
                    wallet      = if (walletResult is Result.Success) walletResult.data else null,
                    remoteSteps = if (stepsResult  is Result.Success) stepsResult.data  else null,
                    error       = if (userResult   is Result.Error)   userResult.message else null
                )
            }
        }
    }

    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio) }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            when (val result = authRepository.changePassword(oldPassword, newPassword)) {
                is Result.Success -> _uiState.update { it.copy(changePasswordSuccess = true) }
                is Result.Error   -> _uiState.update { it.copy(changePasswordError = result.message) }
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun clearPasswordMessages() {
        _uiState.update { it.copy(changePasswordSuccess = false, changePasswordError = null) }
    }
}
