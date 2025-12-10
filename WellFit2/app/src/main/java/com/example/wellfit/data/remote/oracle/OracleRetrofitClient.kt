package com.example.wellfit.data.remote.oracle

import com.example.wellfit.data.remote.OracleAuthApiService
import com.example.wellfit.data.remote.OracleDesafioApiService
import com.example.wellfit.data.remote.OracleEnfermedadApiService
import com.example.wellfit.data.remote.OracleObjetivoApiService
import com.example.wellfit.data.remote.OraclePacienteApiService
import com.example.wellfit.data.remote.OracleUserHealthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object OracleRetrofitClient {

    // TU URL DE ORDS (Asegúrate de que termine en /)
    private const val BASE_URL = "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/"

    // Interceptor para ver los logs de conexión en el Logcat (útil para depurar errores)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // --- Instancias de las Interfaces (Servicios) ---
    val authService: OracleAuthApiService = retrofit.create(OracleAuthApiService::class.java)
    val pacienteService: OraclePacienteApiService = retrofit.create(OraclePacienteApiService::class.java)
    val enfermedadService: OracleEnfermedadApiService = retrofit.create(OracleEnfermedadApiService::class.java)
    val desafioService: OracleDesafioApiService = retrofit.create(OracleDesafioApiService::class.java)
    val objetivoService: OracleObjetivoApiService = retrofit.create(OracleObjetivoApiService::class.java)
    val userHealthService: OracleUserHealthApiService = retrofit.create(OracleUserHealthApiService::class.java)
}