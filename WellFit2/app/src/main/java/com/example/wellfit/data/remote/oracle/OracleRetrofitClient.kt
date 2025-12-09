package com.example.wellfit.data.remote.oracle

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OracleRetrofitClient {

    private const val BASE_URL =
        "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/"

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: OracleAuthService = retrofit.create(OracleAuthService::class.java)
    val apiService: OracleApiService = retrofit.create(OracleApiService::class.java)
}
