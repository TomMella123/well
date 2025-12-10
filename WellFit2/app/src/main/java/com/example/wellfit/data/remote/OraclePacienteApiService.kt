package com.example.wellfit.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// === MODELOS DE DATOS (DTOs) ===

// Auth
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// Paciente
data class PacienteRemoto(
    @SerializedName("id_paciente") val idPaciente: Long? = null,
    @SerializedName("rut_paciente") val rut: Long? = null,
    @SerializedName("dv_paciente") val dv: String? = null,
    @SerializedName("nombre_paciente") val nombre: String? = null,
    @SerializedName("correo_paciente") val correo: String? = null,
    @SerializedName("pass_hash_paciente") val pass: String = "",
    // ... agrega los campos extra que necesites (peso, altura, etc)
    @SerializedName("altura_paciente") val altura: Int? = null,
    @SerializedName("peso_actual") val peso: Int? = null,
    @SerializedName("id_medico") val idMedico: Long? = null,
    @SerializedName("fecha_nacimiento") val fechaNac: String? = null,
    @SerializedName("genero_paciente") val genero: String? = null
)
data class PacienteListResponse(@SerializedName("items") val items: List<PacienteRemoto>)

// Registro
data class PacientePostRequest(
    val rut: Long, val dv: String, val nombre: String, val email: String,
    @SerializedName("fecha_nac") val fechaNac: String, val genero: String,
    val altura: Int, val peso: Int,
    @SerializedName("id_medico") val idMedico: Long,
    val password: String,
    // Lista de IDs de enfermedades seleccionadas en el registro (Para manejo manual)
    val enfermedades: List<Long>? = null
)
data class PacientePostResponse(val mensaje: String?)

// Enfermedad
data class EnfermedadRemota(
    @SerializedName("id_enfermedad") val idEnfermedad: Long,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?
)
data class EnfermedadListResponse(@SerializedName("items") val items: List<EnfermedadRemota>)

// Relación Paciente-Enfermedad (Tabla intermedia)
data class PacienteEnfermedadRutPost(
    val rut: Long, val dv: String,
    @SerializedName("id_enfermedad") val idEnfermedad: Long
)

// Salud
data class UserHealthPostRequest(
    val rut: Long, val dv: String,
    @SerializedName("fecha_data") val fechaData: String,
    @SerializedName("presion_sistolica") val presionSistolica: Int?,
    @SerializedName("presion_diastolica") val presionDiastolica: Int?,
    @SerializedName("glucosa_sangre") val glucosaSangre: Int?,
    @SerializedName("pasos") val pasos: Int?,
    @SerializedName("agua_vasos") val aguaVasos: Int?
)
data class UserHealthListResponse(@SerializedName("items") val items: List<UserHealthItem>)
data class UserHealthItem(@SerializedName("id_data") val idData: Long) // Placeholder

// Desafios y Objetivos (Placeholders para que compile RetrofitClient)
data class DesafioRemoto(val id: Long)
data class DesafioListResponse(val items: List<DesafioRemoto>)
data class ObjetivoRemoto(val id: Long)
data class ObjetivoListResponse(val items: List<ObjetivoRemoto>)


// === INTERFACES API (Services) ===

interface OracleAuthApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}

interface OraclePacienteApiService {
    @GET("v1/gestion/paciente")
    suspend fun obtenerPacientePorCorreo(
        @Header("Authorization") token: String,
        @Query("correo_paciente") correo: String
    ): Response<PacienteListResponse>

    @POST("v1/gestion/paciente")
    suspend fun crearPaciente(
        @Header("Authorization") token: String,
        @Body body: PacientePostRequest
    ): Response<PacientePostResponse>
}

interface OracleEnfermedadApiService {
    @GET("v1/gestion/enfermedad")
    suspend fun obtenerEnfermedades(@Header("Authorization") token: String): Response<EnfermedadListResponse>

    @POST("v1/gestion/enfermedad/pac_enf")
    suspend fun asociarEnfermedadPorRut(
        @Header("Authorization") token: String,
        @Body body: PacienteEnfermedadRutPost
    ): Response<Void>
}

interface OracleUserHealthApiService {
    @POST("v1/gestion/user_health")
    suspend fun crearDatoSalud(
        @Header("Authorization") token: String,
        @Body body: UserHealthPostRequest
    ): Response<Map<String,String>>
}

// Interfaces vacías para cumplir dependencias si aún no tienes el endpoint listo
interface OracleDesafioApiService {
    @GET("v1/gestion/desafio")
    suspend fun obtenerDesafios(@Header("Authorization") token: String): Response<DesafioListResponse>
}
interface OracleObjetivoApiService {
    @GET("v1/gestion/objetivo")
    suspend fun obtenerObjetivos(@Header("Authorization") token: String): Response<ObjetivoListResponse>
}