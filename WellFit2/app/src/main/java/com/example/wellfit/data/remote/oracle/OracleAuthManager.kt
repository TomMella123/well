package com.example.wellfit.data.remote.oracle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials

object OracleAuthManager {

    // ⚠️ RECOMENDACIÓN: Mueve esto a local.properties o build.gradle en el futuro por seguridad
    private const val CLIENT_ID = "3c--eINdA_VFoOTbY5ZEtg.."
    private const val CLIENT_SECRET = "LEsufeK0VkYOZMP9h5Mmpg.."

    private var accessToken: String? = null
    private var expiresAtSeconds: Long = 0L

    // Obtiene un token válido (recicla el existente si no ha expirado)
    suspend fun getValidToken(): String = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis() / 1000

        // Si tenemos token y le queda más de 1 minuto de vida, lo usamos
        if (accessToken != null && now < expiresAtSeconds - 60) {
            return@withContext accessToken!!
        }

        // Si no, pedimos uno nuevo
        refreshToken()
    }

    private suspend fun refreshToken(): String {
        val authHeader = Credentials.basic(CLIENT_ID, CLIENT_SECRET)
        try {
            val resp = OracleRetrofitClient.authService.getToken(
                auth = authHeader,
                grantType = "client_credentials"
            )
            val now = System.currentTimeMillis() / 1000
            accessToken = resp.accessToken
            expiresAtSeconds = now + resp.expiresIn
            return resp.accessToken
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Lanzar error si no hay internet o credenciales mal
        }
    }

    suspend fun getBearerHeader(): String {
        return "Bearer ${getValidToken()}"
    }
}