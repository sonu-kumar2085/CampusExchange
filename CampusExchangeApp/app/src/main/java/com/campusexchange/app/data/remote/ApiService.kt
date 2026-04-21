package com.campusexchange.app.data.remote

import com.campusexchange.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiUserResponse>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/logout")
    suspend fun logout(): Response<ApiGenericResponse>

    @POST("users/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiGenericResponse>

    // ── User ──────────────────────────────────────────────────────────────────

    @GET("users/current-user")
    suspend fun getCurrentUser(): Response<ApiUserResponse>

    @GET("users/wallet")
    suspend fun getWallet(): Response<ApiWalletResponse>

    @GET("users/steps")
    suspend fun getSteps(): Response<ApiStepsResponse>

    @POST("users/steps/update")
    suspend fun updateSteps(@Body request: UpdateStepsRequest): Response<ApiStepsResponse>

    // ── Stocks ────────────────────────────────────────────────────────────────

    @GET("stocks")
    suspend fun getAllStocks(): Response<ApiStocksResponse>

    @GET("stocks/portfolio")
    suspend fun getPortfolio(): Response<ApiPortfolioResponse>

    @POST("stocks/order")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<ApiOrderResponse>

    @GET("stocks/orders")
    suspend fun getMyOrders(): Response<ApiOrdersResponse>

    // ── Bets ──────────────────────────────────────────────────────────────────

    @GET("bet/allbets")
    suspend fun getAllBets(): Response<ApiBetsResponse>

    @POST("bet/enroll")
    suspend fun enrollBet(@Body request: EnrollRequest): Response<ApiEnrollResponse>

    @GET("bet/mybets")
    suspend fun getMyBets(): Response<ApiMyBetsResponse>

    // ── Wallet / Leaderboard ──────────────────────────────────────────────────

    @GET("wallets/leaderboard")
    suspend fun getLeaderboard(): Response<ApiLeaderboardResponse>
}
