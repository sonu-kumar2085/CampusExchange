package com.campusexchange.app.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusexchange.app.data.remote.dto.StockDto
import com.campusexchange.app.data.repository.Result
import com.campusexchange.app.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StockWithChange(
    val stock: StockDto,
    val prevPrice: Double = 0.0,
    val change24h: Double = 0.0  // computed from polling
)

data class MarketUiState(
    val isLoading: Boolean = false,
    val stocks: List<StockWithChange> = emptyList(),
    val error: String? = null,
    val orderSuccess: String? = null,
    val orderError: String? = null
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState(isLoading = true))
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private val priceHistory = mutableMapOf<String, Double>()

    init {
        startPolling()
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchStocks()
                delay(5_000)
            }
        }
    }

    private suspend fun fetchStocks() {
        when (val result = stockRepository.getAllStocks()) {
            is Result.Success -> {
                val stocks = result.data.map { stock ->
                    val prev = priceHistory[stock.stockId] ?: stock.price
                    val change = if (prev != 0.0) ((stock.price - prev) / prev) * 100.0 else 0.0
                    priceHistory[stock.stockId] = stock.price
                    StockWithChange(stock, prev, change)
                }
                _uiState.update { it.copy(isLoading = false, stocks = stocks, error = null) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
            else -> {}
        }
    }

    fun placeOrder(stockId: String, quantity: Int, limitPrice: Double, type: String) {
        viewModelScope.launch {
            when (val result = stockRepository.placeOrder(stockId, quantity, limitPrice, type)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(orderSuccess = "${type.uppercase()} order placed for $quantity shares @ $limitPrice coins")
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(orderError = result.message) }
                }
                else -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(orderSuccess = null, orderError = null) }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
