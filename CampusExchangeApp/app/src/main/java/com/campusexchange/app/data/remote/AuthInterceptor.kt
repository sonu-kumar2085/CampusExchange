package com.campusexchange.app.data.remote

import com.campusexchange.app.data.local.TokenDataStore
import com.campusexchange.app.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore,
    private val baseUrl: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataStore.getAccessToken().first() }
        val request = buildRequest(chain.request(), token)
        var response = chain.proceed(request)

        // Token expired — try to refresh
        if (response.code == 401) {
            response.close()
            val newToken = runBlocking { tryRefreshToken() }
            if (newToken != null) {
                val retryRequest = buildRequest(chain.request(), newToken)
                response = chain.proceed(retryRequest)
            }
        }
        return response
    }

    private fun buildRequest(original: Request, token: String?): Request {
        val builder = original.newBuilder()
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        return builder.build()
    }

    private suspend fun tryRefreshToken(): String? {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken().first() ?: return null

            // Create a temporary Retrofit instance (no auth interceptor to avoid loops)
            val tempRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val tempService = tempRetrofit.create(ApiService::class.java)

            val response = tempService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                val newAccess = response.body()?.data?.accessToken
                val newRefresh = response.body()?.data?.refreshToken
                if (newAccess != null && newRefresh != null) {
                    tokenDataStore.saveTokens(newAccess, newRefresh)
                    return newAccess
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}
