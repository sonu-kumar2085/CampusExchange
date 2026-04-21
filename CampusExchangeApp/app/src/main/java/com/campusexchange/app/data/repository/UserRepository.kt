package com.campusexchange.app.data.repository

import com.campusexchange.app.data.remote.ApiService
import com.campusexchange.app.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getCurrentUser(): Result<UserDto> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                Result.Error("Failed to load user", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun getWallet(): Result<WalletDto> {
        return try {
            val response = api.getWallet()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                Result.Error("Failed to load wallet", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}

@Singleton
class WalletRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getLeaderboard(): Result<List<LeaderboardEntryDto>> {
        return try {
            val response = api.getLeaderboard()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                Result.Error("Failed to load leaderboard", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}
