package com.example.wellfit.data.remote

import com.example.wellfit.data.local.dto.HistorialPesoDto
import com.example.wellfit.data.local.dto.PacienteDto
import com.example.wellfit.data.local.dto.UserHealthDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // ===============================
    //  ENDPOINTS QUE YA TENÍAS
    //  (no los cambio)
    // ===============================

    @POST("pacientes")
    suspend fun crearPaciente(@Body dto: PacienteDto): Response<PacienteDto>

    @POST("pacientes/login")
    suspend fun login(@Body body: Map<String, String>): Response<PacienteDto?>

    @GET("pacientes/{id}")
    suspend fun obtenerPaciente(@Path("id") id: Int): PacienteDto

    @GET("pacientes/correo/{correo}")
    suspend fun obtenerPorCorreo(@Path("correo") correo: String): PacienteDto?

    // ===============================
    //  BATCH SYNC SALUD
    // ===============================

    // Enviar lote de registros de salud
    @POST("health-data/batch")
    suspend fun enviarHealthData(
        @Body datos: List<UserHealthDataDto>
    ): Response<Unit>

    // Enviar lote de historial de peso
    @POST("historial-peso/batch")
    suspend fun enviarHistorialPeso(
        @Body datos: List<HistorialPesoDto>
    ): Response<Unit>
}
