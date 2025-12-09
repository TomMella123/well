package com.example.wellfit.data.remote.api

import com.example.wellfit.data.local.dto.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface OracleAuthApiService {

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Header("Authorization") auth: String,          // "Basic *****"
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}
