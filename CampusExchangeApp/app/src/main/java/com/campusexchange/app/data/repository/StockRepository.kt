package com.campusexchange.app.data.repository

import com.campusexchange.app.data.remote.ApiService
import com.campusexchange.app.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAllStocks(): Result<List<StockDto>> {
        return try {
            val response = api.getAllStocks()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                Result.Error("Failed to load stocks", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun getPortfolio(): Result<List<PortfolioItemDto>> {
        return try {
            val response = api.getPortfolio()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else if (response.code() == 404) {
                Result.Success(emptyList())
            } else {
                Result.Error("Failed to load portfolio", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun placeOrder(stockId: String, quantity: Int, limitPrice: Double, type: String): Result<StockTradeDto> {
        return try {
            val response = api.placeOrder(PlaceOrderRequest(stockId, quantity, limitPrice, type))
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                val msg = response.errorBody()?.string() ?: "Order failed"
                Result.Error(parseErrorMessage(msg), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun getMyOrders(): Result<List<StockTradeDto>> {
        return try {
            val response = api.getMyOrders()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else if (response.code() == 404) {
                Result.Success(emptyList())
            } else {
                Result.Error("Failed to load orders", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    private fun parseErrorMessage(json: String): String {
        return try {
            val jsonObject = org.json.JSONObject(json)
            when {
                jsonObject.has("message") -> jsonObject.getString("message")
                jsonObject.has("error")   -> jsonObject.getString("error")
                else                      -> json
            }
        } catch (e: Exception) {
            if (json.contains("<html", ignoreCase = true))
                "An unexpected server error occurred."
            else
                json
        }
    }
}
