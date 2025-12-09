package com.example.wellfit.data.remote.oracle

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface OracleAuthService {

    // POST https://.../ords/wellfit_admin/oauth/token
    @FormUrlEncoded
    @POST("ords/wellfit_admin/oauth/token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String,          // Basic xxx
        @Field("grant_type") grantType: String = "client_credentials"
    ): OracleTokenResponse
}
