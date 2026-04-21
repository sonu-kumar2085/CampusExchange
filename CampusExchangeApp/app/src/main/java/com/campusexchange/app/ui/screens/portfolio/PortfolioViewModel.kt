package com.campusexchange.app.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.remote.dto.PortfolioItemDto
import com.campusexchange.app.data.remote.dto.StockDto
import com.campusexchange.app.data.remote.dto.StockTradeDto
import com.campusexchange.app.data.repository.Result
import com.campusexchange.app.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val holdings: List<PortfolioItemDto> = emptyList(),
    val pendingOrders: List<StockTradeDto> = emptyList(),
    val allStocks: List<StockDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState(isLoading = true))
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val portResult = stockRepository.getPortfolio()
            val ordersResult = stockRepository.getMyOrders()
            val stocksResult = stockRepository.getAllStocks()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    holdings = if (portResult is Result.Success) portResult.data else emptyList(),
                    pendingOrders = if (ordersResult is Result.Success) ordersResult.data else emptyList(),
                    allStocks = if (stocksResult is Result.Success) stocksResult.data else emptyList(),
                    error = if (portResult is Result.Error) portResult.message else null
                )
            }
        }
    }
}
