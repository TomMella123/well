package com.example.wellfit.data.remote.oracle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials

object OracleAuthManager {

    // ⚠ En producción NO deberías hardcodear esto.
    private const val CLIENT_ID = "3c--eINdA_VFoOTbY5ZEtg.."
    private const val CLIENT_SECRET = "LEsufeK0VkYOZMP9h5Mmpg.."

    private var accessToken: String? = null
    private var expiresAtSeconds: Long = 0L

    private suspend fun refreshToken(): String = withContext(Dispatchers.IO) {
        val authHeader = Credentials.basic(CLIENT_ID, CLIENT_SECRET)

        val resp = OracleRetrofitClient.authService.getToken(
            authHeader = authHeader,
            grantType = "client_credentials"
        )

        val now = System.currentTimeMillis() / 1000
        accessToken = resp.accessToken
        expiresAtSeconds = now + resp.expiresIn

        accessToken!!
    }

    suspend fun getValidToken(): String = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis() / 1000

        val token = accessToken
        if (token != null && now < expiresAtSeconds - 60) {
            // aún es válido
            token
        } else {
            refreshToken()
        }
    }

    suspend fun getBearerHeader(): String {
        val token = getValidToken()
        return "Bearer $token"
    }
}
