package com.campusexchange.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.local.StepEntity
import com.campusexchange.app.data.remote.dto.StepsDto
import com.campusexchange.app.data.remote.dto.UserDto
import com.campusexchange.app.data.remote.dto.WalletDto
import com.campusexchange.app.data.repository.Result
import com.campusexchange.app.data.repository.StepRepository
import com.campusexchange.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val wallet: WalletDto? = null,
    val remoteSteps: StepsDto? = null,
    val localSteps: StepEntity? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
        observeLocalSteps()
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
                    user        = if (userResult is Result.Success) userResult.data else null,
                    wallet      = if (walletResult is Result.Success) walletResult.data else null,
                    remoteSteps = if (stepsResult is Result.Success) stepsResult.data else null,
                    error       = if (userResult is Result.Error) userResult.message else null
                )
            }
        }
    }

    private fun observeLocalSteps() {
        viewModelScope.launch {
            stepRepository.getLocalSteps().collect { entity ->
                _uiState.update { it.copy(localSteps = entity) }
            }
        }
    }
}
