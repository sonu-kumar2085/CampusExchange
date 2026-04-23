package com.campusexchange.app.data.repository

import com.campusexchange.app.data.local.TokenDataStore
import com.campusexchange.app.data.remote.ApiService
import com.campusexchange.app.data.remote.dto.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = -1) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun register(fullName: String, email: String, username: String, password: String): Result<LoginData> {
        return try {
            val response = api.register(RegisterRequest(fullName, email, username, password))
            if (response.isSuccessful) {
                val data = response.body()!!.data
                tokenDataStore.saveTokens(data.accessToken, data.refreshToken)
                tokenDataStore.saveUsername(data.user.username)
                Result.Success(data)
            } else {
                val msg = response.errorBody()?.string() ?: "Registration failed"
                Result.Error(parseErrorMessage(msg), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun login(emailOrUsername: String, password: String, isEmail: Boolean): Result<LoginData> {
        return try {
            val request = if (isEmail)
                LoginRequest(email = emailOrUsername, password = password)
            else
                LoginRequest(username = emailOrUsername, password = password)

            val response = api.login(request)
            if (response.isSuccessful) {
                val data = response.body()!!.data
                tokenDataStore.saveTokens(data.accessToken, data.refreshToken)
                tokenDataStore.saveUsername(data.user.username)
                Result.Success(data)
            } else {
                val msg = response.errorBody()?.string() ?: "Login failed"
                Result.Error(parseErrorMessage(msg), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            tokenDataStore.clearAll()
            Result.Success(Unit)
        } catch (e: Exception) {
            tokenDataStore.clearAll()
            Result.Success(Unit)
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = api.changePassword(ChangePasswordRequest(oldPassword, newPassword))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to change password"
                Result.Error(parseErrorMessage(msg), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    fun isLoggedIn() = tokenDataStore.isLoggedIn()

    private fun parseErrorMessage(json: String): String {
        return try {
            val jsonObject = org.json.JSONObject(json)
            if (jsonObject.has("message")) {
                jsonObject.getString("message")
            } else if (jsonObject.has("error")) {
                jsonObject.getString("error")
            } else {
                json
            }
        } catch (e: Exception) {
            if (json.contains("<html", ignoreCase = true)) {
                "An unexpected server error occurred."
            } else {
                json
            }
        }
    }
}
