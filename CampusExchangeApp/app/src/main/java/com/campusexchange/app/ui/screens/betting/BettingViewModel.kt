package com.campusexchange.app.ui.screens.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.remote.dto.BetDto
import com.campusexchange.app.data.remote.dto.EnrolledBetDto
import com.campusexchange.app.data.repository.BetRepository
import com.campusexchange.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BettingUiState(
    val isLoading: Boolean = false,
    val allBets: List<BetDto> = emptyList(),
    val myBets: List<EnrolledBetDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class BettingViewModel @Inject constructor(
    private val betRepository: BetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BettingUiState(isLoading = true))
    val uiState: StateFlow<BettingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val betsResult = betRepository.getAllBets()
            val myBetsResult = betRepository.getMyBets()

            val bets = if (betsResult is Result.Success) betsResult.data else emptyList()
            val myBets = if (myBetsResult is Result.Success) myBetsResult.data else emptyList()
            val error = if (betsResult is Result.Error) betsResult.message else null

            _uiState.update { it.copy(isLoading = false, allBets = bets, myBets = myBets, error = error) }
        }
    }

    fun enrollBet(betId: String, response: String, coins: Double) {
        viewModelScope.launch {
            when (val result = betRepository.enrollBet(betId, response, coins)) {
                is Result.Success -> {
                    _uiState.update { it.copy(successMessage = "Enrolled! You picked '$response' with $coins coins 🎲") }
                    loadData()
                }
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                else -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
