package com.campusexchange.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Auth DTOs ──────────────────────────────────────────────────────────────

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val username: String,
    val password: String
)

data class LoginRequest(
    val email: String? = null,
    val username: String? = null,
    val password: String
)

data class LoginResponse(
    val statusCode: Int,
    val data: LoginData,
    val message: String,
    val success: Boolean
)

data class LoginData(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val statusCode: Int,
    val data: RefreshData,
    val message: String,
    val success: Boolean
)

data class RefreshData(
    val accessToken: String,
    val refreshToken: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

// ── User DTOs ──────────────────────────────────────────────────────────────

data class UserDto(
    @SerializedName("_id") val id: String = "",
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val college: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ApiUserResponse(
    val statusCode: Int,
    val data: UserDto,
    val message: String,
    val success: Boolean
)

// ── Wallet DTOs ─────────────────────────────────────────────────────────────

data class WalletDto(
    @SerializedName("_id") val id: String = "",
    val username: String = "",
    val campusCoins: Double = 0.0
)

data class ApiWalletResponse(
    val statusCode: Int,
    val data: WalletDto,
    val message: String,
    val success: Boolean
)

// ── Steps DTOs ──────────────────────────────────────────────────────────────

data class StepsDto(
    @SerializedName("_id") val id: String = "",
    val username: String = "",
    val stepsCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ApiStepsResponse(
    val statusCode: Int,
    val data: StepsDto,
    val message: String,
    val success: Boolean
)

data class UpdateStepsRequest(
    val stepsCount: Int
)

// ── Stock DTOs ──────────────────────────────────────────────────────────────

data class StockDto(
    @SerializedName("_id") val id: String = "",
    val stockId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val sharesct: Int = 100,
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ApiStocksResponse(
    val statusCode: Int,
    val data: List<StockDto>,
    val message: String,
    val success: Boolean
)

data class PlaceOrderRequest(
    val stockId: String,
    val quantity: Int,
    val limitPrice: Double,
    val type: String  // "buy" or "sell"
)

data class StockTradeDto(
    @SerializedName("_id") val id: String = "",
    val username: String = "",
    val stockId: String = "",
    val quantity: Int = 0,
    val limitPrice: Double = 0.0,
    val type: String = "",
    val status: String = "",
    val createdAt: String = ""
)

data class ApiOrderResponse(
    val statusCode: Int,
    val data: StockTradeDto,
    val message: String,
    val success: Boolean
)

data class ApiOrdersResponse(
    val statusCode: Int,
    val data: List<StockTradeDto>,
    val message: String,
    val success: Boolean
)

data class PortfolioItemDto(
    val stockId: String = "",
    val quantity: Int = 0
)

data class ApiPortfolioResponse(
    val statusCode: Int,
    val data: List<PortfolioItemDto>,
    val message: String,
    val success: Boolean
)

// ── Bet DTOs ─────────────────────────────────────────────────────────────────

data class BetDto(
    @SerializedName("_id") val id: String = "",
    val betId: String = "",
    val question: String = "",
    val description: String? = null,
    val status: String = "open",
    val result: String? = null,
    val totalEnrolled: Int = 0,
    val totalPool: Double = 0.0,
    val resultTime: String = "",
    val createdAt: String = ""
)

data class ApiBetsResponse(
    val statusCode: Int,
    val data: List<BetDto>,
    val message: String,
    val success: Boolean
)

data class EnrollRequest(
    val betId: String,
    val response: String,   // "yes" or "no"
    val campusCoins: Double
)

data class EnrollDto(
    @SerializedName("_id") val id: String = "",
    val betId: String = "",
    val username: String = "",
    val campusCoins: Double = 0.0,
    val response: String = "",
    val createdAt: String = ""
)

data class ApiEnrollResponse(
    val statusCode: Int,
    val data: EnrollDto,
    val message: String,
    val success: Boolean
)

data class EnrolledBetDto(
    @SerializedName("_id") val id: String = "",
    val betId: String = "",
    val question: String = "",
    val description: String? = null,
    val status: String = "open",
    val result: String? = null,
    val totalEnrolled: Int = 0,
    val totalPool: Double = 0.0,
    val resultTime: String = "",
    val myResponse: String = "",
    val myCoins: Double = 0.0,
    val enrolledAt: String = ""
)

data class ApiMyBetsResponse(
    val statusCode: Int,
    val data: List<EnrolledBetDto>,
    val message: String,
    val success: Boolean
)

// ── Leaderboard DTOs ──────────────────────────────────────────────────────────

data class LeaderboardEntryDto(
    val username: String = "",
    val campusCoins: Double = 0.0
)

data class ApiLeaderboardResponse(
    val statusCode: Int,
    val data: List<LeaderboardEntryDto>,
    val message: String,
    val success: Boolean
)

// ── Generic response ──────────────────────────────────────────────────────────

data class ApiGenericResponse(
    val statusCode: Int,
    val data: Any? = null,
    val message: String,
    val success: Boolean
)
