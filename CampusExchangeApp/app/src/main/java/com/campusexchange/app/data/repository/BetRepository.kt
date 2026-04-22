package com.campusexchange.app.data.repository

import com.campusexchange.app.data.remote.ApiService
import com.campusexchange.app.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAllBets(): Result<List<BetDto>> {
        return try {
            val response = api.getAllBets()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else if (response.code() == 404) {
                Result.Success(emptyList())
            } else {
                Result.Error("Failed to load bets", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun enrollBet(betId: String, response: String, campusCoins: Double): Result<EnrollDto> {
        return try {
            val resp = api.enrollBet(EnrollRequest(betId, response, campusCoins))
            if (resp.isSuccessful) {
                Result.Success(resp.body()!!.data)
            } else {
                val msg = resp.errorBody()?.string() ?: "Enrollment failed"
                Result.Error(parseErrorMessage(msg), resp.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun getMyBets(): Result<List<EnrolledBetDto>> {
        return try {
            val response = api.getMyBets()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else if (response.code() == 404) {
                Result.Success(emptyList())
            } else {
                Result.Error("Failed to load my bets", response.code())
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
