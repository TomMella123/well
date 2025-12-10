package com.example.wellfit.data.remote

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// ==========================================
// 1. MODELOS DE DATOS (DTOs)
// ==========================================

// --- Auth ---
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// --- Paciente ---
data class PacienteRemoto(
    @SerializedName("id_paciente") val idPaciente: Long? = null,
    @SerializedName("nombre_paciente") val nombre: String? = null,
    @SerializedName("correo_paciente") val correo: String? = null,
    @SerializedName("pass_hash_paciente") val pass: String = "",
    // Agrega más campos si la respuesta los trae
    @SerializedName("rut_paciente") val rut: Long? = null
)
data class PacienteListResponse(@SerializedName("items") val items: List<PacienteRemoto>)

data class PacientePostRequest(
    val rut: Long, val dv: String, val nombre: String, val email: String,
    @SerializedName("fecha_nac") val fechaNac: String,
    val genero: String, val altura: Int, val peso: Int,
    @SerializedName("id_medico") val idMedico: Long,
    val password: String,
    @SerializedName("image_id") val imageId: Long? = null,
    val enfermedades: List<Long>? = null // IDs de enfermedades para asociar
)
data class PacientePostResponse(val mensaje: String?) // O lo que devuelva tu API al crear

// --- Enfermedad ---
data class EnfermedadRemota(
    @SerializedName("id_enfermedad") val idEnfermedad: Long,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?,
    @SerializedName("descripcion_enfermedad") val descripcionEnfermedad: String?
)
data class EnfermedadListResponse(@SerializedName("items") val items: List<EnfermedadRemota>)

data class PacienteEnfermedadRutPost(
    val rut: Long, val dv: String, @SerializedName("id_enfermedad") val idEnfermedad: Long
)

// --- Desafio ---
data class DesafioRemoto(
    @SerializedName("id_desafio") val idDesafio: Long,
    @SerializedName("nombre_desafio") val nombreDesafio: String?,
    @SerializedName("descripcion_desafio") val descripcionDesafio: String?,
    @SerializedName("puntaje") val puntaje: Int?,
    @SerializedName("id_dificultad") val idDificultad: Int?,
    @SerializedName("desafio_imagen") val desafioImagen: String? = null
)
data class DesafioListResponse(@SerializedName("items") val items: List<DesafioRemoto>)

// --- Ejercicio ---
data class EjercicioRemoto(
    @SerializedName("id_ejercicio") val idEjercicio: Long,
    @SerializedName("nombre_ejercicio") val nombreEjercicio: String?,
    @SerializedName("descripcion_ejercicio") val descripcionEjercicio: String?,
    @SerializedName("series") val series: Int?,
    @SerializedName("repeticiones") val repeticiones: Int?,
    @SerializedName("id_dificultad") val idDificultad: Int?
)
data class EjercicioListResponse(@SerializedName("items") val items: List<EjercicioRemoto>)

// --- Receta ---
data class RecetaRemota(
    @SerializedName("id_receta") val idReceta: Long,
    @SerializedName("nombre_receta") val nombreReceta: String?,
    @SerializedName("descripcion_receta") val descripcionReceta: String?,
    @SerializedName("calorias") val calorias: Int?,
    @SerializedName("tiempo_preparacion") val tiempo: Int?
)
data class RecetaListResponse(@SerializedName("items") val items: List<RecetaRemota>)

// --- Objetivo ---
data class ObjetivoRemoto(
    @SerializedName("objetivo_id") val idObjetivo: Long,
    @SerializedName("nombre_objetivo") val nombreObjetivo: String?
)
data class ObjetivoListResponse(@SerializedName("items") val items: List<ObjetivoRemoto>)

// --- Medico ---
data class MedicoRemoto(
    @SerializedName("id_medico") val idMedico: Long,
    @SerializedName("nombre_medico") val nombreMedico: String?
)
data class MedicoListResponse(@SerializedName("items") val items: List<MedicoRemoto>)

// --- Salud (User Health) ---
// CAMBIO: Usamos id_paciente, NO rut
data class UserHealthPostRequest(
    @SerializedName("id_paciente") val idPaciente: Long,
    @SerializedName("fecha_data") val fechaData: String,
    @SerializedName("presion_sistolica") val presionSistolica: Int?,
    @SerializedName("presion_diastolica") val presionDiastolica: Int?,
    @SerializedName("glucosa_sangre") val glucosaSangre: Int?,
    @SerializedName("agua_vasos") val aguaVasos: Int?,
    @SerializedName("pasos") val pasos: Int?
)

// ==========================================
// 2. INTERFACES API (Tus URLs)
// ==========================================

interface OracleAuthApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}

interface OracleApiService {

    // --- Pacientes ---
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

    // --- Enfermedades ---
    @GET("v1/gestion/enfermedad")
    suspend fun obtenerEnfermedades(@Header("Authorization") token: String): Response<EnfermedadListResponse>

    @POST("v1/gestion/enfermedad/pac_enf")
    suspend fun asociarEnfermedad(
        @Header("Authorization") token: String,
        @Body body: PacienteEnfermedadRutPost
    ): Response<Void>

    // --- Desafíos ---
    @GET("v1/gestion/desafio")
    suspend fun obtenerDesafios(@Header("Authorization") token: String): Response<DesafioListResponse>

    // --- Ejercicios ---
    @GET("v1/gestion/ejercicio")
    suspend fun obtenerEjercicios(@Header("Authorization") token: String): Response<EjercicioListResponse>

    // --- Recetas ---
    @GET("v1/gestion/receta")
    suspend fun obtenerRecetas(@Header("Authorization") token: String): Response<RecetaListResponse>

    // --- Objetivos ---
    @GET("v1/gestion/objetivo")
    suspend fun obtenerObjetivos(@Header("Authorization") token: String): Response<ObjetivoListResponse>

    // --- Médicos ---
    @GET("v1/gestion/medico")
    suspend fun obtenerMedicos(@Header("Authorization") token: String): Response<MedicoListResponse>

    // --- Salud ---
    @POST("v1/gestion/user_health")
    suspend fun crearDatoSalud(
        @Header("Authorization") token: String,
        @Body body: UserHealthPostRequest
    ): Response<Map<String, String>>
}

// ==========================================
// 3. DATASOURCE CENTRALIZADO
// ==========================================

object OracleRemoteDataSource {

    private const val BASE_URL = "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(OracleAuthApiService::class.java)
    private val apiService = retrofit.create(OracleApiService::class.java)

    // Token
    private var accessToken: String? = null
    private var expiresAt: Long = 0

    private suspend fun getBearer(): String {
        val now = System.currentTimeMillis() / 1000
        if (accessToken != null && now < expiresAt - 60) return "Bearer $accessToken"

        // Pide tu token (Asegura poner tus credenciales reales aquí)
        val basic = Credentials.basic("3c--eINdA_VFoOTbY5ZEtg..", "LEsufeK0VkYOZMP9h5Mmpg..")
        return try {
            val resp = authService.getToken(basic)
            accessToken = resp.accessToken
            expiresAt = now + resp.expiresIn
            "Bearer ${resp.accessToken}"
        } catch (e: Exception) { "" }
    }

    // --- FUNCIONES PÚBLICAS ---

    suspend fun loginPaciente(correo: String, pass: String): PacienteRemoto? {
        return try {
            val resp = apiService.obtenerPacientePorCorreo(getBearer(), correo)
            if (resp.isSuccessful) {
                val p = resp.body()?.items?.firstOrNull()
                // Validación simple de contraseña
                if (p != null && p.pass == pass) return p
            }
            null
        } catch (e: Exception) { null }
    }

    suspend fun crearPaciente(req: PacientePostRequest): Boolean {
        return try {
            val resp = apiService.crearPaciente(getBearer(), req)
            if (resp.isSuccessful) {
                if (!req.enfermedades.isNullOrEmpty()) {
                    // Lógica para asociar enfermedades (requiere RUT para la tabla intermedia)
                    req.enfermedades.forEach { idEnf ->
                        apiService.asociarEnfermedad(getBearer(), PacienteEnfermedadRutPost(req.rut, req.dv, idEnf))
                    }
                }
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun obtenerEnfermedades(): List<EnfermedadRemota> {
        return try {
            val resp = apiService.obtenerEnfermedades(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerObjetivos(): List<ObjetivoRemoto> {
        return try {
            val resp = apiService.obtenerObjetivos(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerMedicos(): List<MedicoRemoto> {
        return try {
            val resp = apiService.obtenerMedicos(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerDesafios(): List<DesafioRemoto> {
        return try {
            val resp = apiService.obtenerDesafios(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerEjercicios(): List<EjercicioRemoto> {
        return try {
            val resp = apiService.obtenerEjercicios(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerRecetas(): List<RecetaRemoto> {
        return try {
            val resp = apiService.obtenerRecetas(getBearer())
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    // --- SALUD (Con ID Paciente) ---
    suspend fun registrarSalud(
        idPaciente: Long,
        presionSis: Int?, presionDias: Int?,
        glucosa: Int?, agua: Int?, pasos: Int?
    ): Boolean {
        return try {
            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale("es", "CL")).format(Date())
            val body = UserHealthPostRequest(idPaciente, fecha, presionSis, presionDias, glucosa, agua, pasos)
            val resp = apiService.crearDatoSalud(getBearer(), body)
            resp.isSuccessful
        } catch (e: Exception) { false }
    }
}