package com.example.wellfit.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// --- MODELOS (DTOs) ---

data class TokenResponse(@SerializedName("access_token") val accessToken: String, @SerializedName("expires_in") val expiresIn: Int)

data class PacienteRemoto(
    @SerializedName("id_paciente") val idPaciente: Long? = null,
    @SerializedName("nombre_paciente") val nombre: String? = null,
    @SerializedName("correo_paciente") val correo: String? = null,
    @SerializedName("pass_hash_paciente") val pass: String = ""
)
data class PacienteListResponse(@SerializedName("items") val items: List<PacienteRemoto>)

data class PacientePostRequest(
    val rut: Long, val dv: String, val nombre: String, val email: String,
    @SerializedName("fecha_nac") val fechaNac: String,
    val genero: String, val altura: Int, val peso: Int,
    @SerializedName("id_medico") val idMedico: Long,
    val password: String,
    val enfermedades: List<Long>? = null,
    @SerializedName("image_id") val imageId: Long? = null
)
data class PacientePostResponse(val mensaje: String?)

data class EnfermedadRemota(@SerializedName("id_enfermedad") val idEnfermedad: Long, @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?)
data class EnfermedadListResponse(@SerializedName("items") val items: List<EnfermedadRemota>)

data class PacienteEnfermedadRutPost(val rut: Long, val dv: String, @SerializedName("id_enfermedad") val idEnfermedad: Long)

data class DesafioRemoto(
    @SerializedName("id_desafio") val idDesafio: Long,
    @SerializedName("nombre_desafio") val nombreDesafio: String?,
    @SerializedName("descripcion_desafio") val descripcionDesafio: String?,
    @SerializedName("puntaje") val puntaje: Int?,
    @SerializedName("id_dificultad") val idDificultad: Int?
)
data class DesafioListResponse(@SerializedName("items") val items: List<DesafioRemoto>)

data class EjercicioRemoto(
    @SerializedName("id_ejercicio") val idEjercicio: Long,
    @SerializedName("nombre_ejercicio") val nombreEjercicio: String?,
    @SerializedName("descripcion_ejercicio") val descripcionEjercicio: String?,
    @SerializedName("series") val series: Int?,
    @SerializedName("repeticiones") val repeticiones: Int?
)
data class EjercicioListResponse(@SerializedName("items") val items: List<EjercicioRemoto>)

data class RecetaRemota(@SerializedName("id_receta") val idReceta: Long, @SerializedName("nombre_receta") val nombreReceta: String?, @SerializedName("descripcion_receta") val descripcionReceta: String?)
data class RecetaListResponse(@SerializedName("items") val items: List<RecetaRemota>)

data class ObjetivoRemoto(@SerializedName("objetivo_id") val idObjetivo: Long, @SerializedName("nombre_objetivo") val nombreObjetivo: String?)
data class ObjetivoListResponse(@SerializedName("items") val items: List<ObjetivoRemoto>)

data class UserHealthPostRequest(
    @SerializedName("id_paciente") val idPaciente: Long,
    @SerializedName("fecha_data") val fechaData: String,
    @SerializedName("presion_sistolica") val presionSistolica: Int?,
    @SerializedName("presion_diastolica") val presionDiastolica: Int?,
    @SerializedName("glucosa_sangre") val glucosaSangre: Int?,
    @SerializedName("agua_vasos") val aguaVasos: Int?,
    @SerializedName("pasos") val pasos: Int?
)
data class UserHealthItem(
    @SerializedName("id_data") val idData: Long,
    @SerializedName("fecha_data") val fechaData: String,
    @SerializedName("presion_sistolica") val presionSistolica: Int?,
    @SerializedName("presion_diastolica") val presionDiastolica: Int?,
    @SerializedName("glucosa_sangre") val glucosaSangre: Int?,
    @SerializedName("agua_vasos") val aguaVasos: Int?,
    @SerializedName("pasos") val pasos: Int?,
    @SerializedName("id_paciente") val idPaciente: Long?
)
data class UserHealthListResponse(@SerializedName("items") val items: List<UserHealthItem>)

// --- INTERFACES API ---

interface OracleAuthApiService {
    @FormUrlEncoded @POST("oauth/token")
    suspend fun getToken(@Header("Authorization") auth: String, @Field("grant_type") grant: String = "client_credentials"): TokenResponse
}

interface OracleApiService {
    @GET("v1/gestion/paciente")
    suspend fun obtenerPacientePorCorreo(@Header("Authorization") token: String, @Query("correo_paciente") correo: String): Response<PacienteListResponse>

    @GET("v1/gestion/paciente/{id}")
    suspend fun obtenerPacientePorId(@Header("Authorization") token: String, @Path("id") id: Long): Response<PacienteRemoto>

    @POST("v1/gestion/paciente")
    suspend fun crearPaciente(@Header("Authorization") token: String, @Body body: PacientePostRequest): Response<PacientePostResponse>

    @GET("v1/gestion/enfermedad")
    suspend fun obtenerEnfermedades(@Header("Authorization") token: String): Response<EnfermedadListResponse>

    @POST("v1/gestion/enfermedad/pac_enf")
    suspend fun asociarEnfermedad(@Header("Authorization") token: String, @Body body: PacienteEnfermedadRutPost): Response<Void>

    @GET("v1/gestion/desafio")
    suspend fun obtenerDesafios(@Header("Authorization") token: String): Response<DesafioListResponse>

    @GET("v1/gestion/ejercicio")
    suspend fun obtenerEjercicios(@Header("Authorization") token: String): Response<EjercicioListResponse>

    @GET("v1/gestion/receta")
    suspend fun obtenerRecetas(@Header("Authorization") token: String): Response<RecetaListResponse>

    @GET("v1/gestion/objetivo")
    suspend fun obtenerObjetivos(@Header("Authorization") token: String): Response<ObjetivoListResponse>

    @POST("v1/gestion/user_health")
    suspend fun crearDatoSalud(@Header("Authorization") token: String, @Body body: UserHealthPostRequest): Response<Map<String, String>>

    @GET("v1/gestion/user_health")
    suspend fun obtenerDatosSalud(@Header("Authorization") token: String, @Query("id_paciente") id: Long): Response<UserHealthListResponse>
}

// --- DATASOURCE ---

object OracleRemoteDataSource {
    private const val BASE_URL = "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(OracleAuthApiService::class.java)
    private val apiService = retrofit.create(OracleApiService::class.java)

    private var accessToken: String? = null
    private var expiresAt: Long = 0

    private suspend fun getBearer(): String {
        val now = System.currentTimeMillis() / 1000
        if (accessToken != null && now < expiresAt - 60) return "Bearer $accessToken"
        return try {
            val resp = authService.getToken(Credentials.basic("3c--eINdA_VFoOTbY5ZEtg..", "LEsufeK0VkYOZMP9h5Mmpg.."))
            accessToken = resp.accessToken; expiresAt = now + resp.expiresIn
            "Bearer ${resp.accessToken}"
        } catch (e: Exception) { "" }
    }

    suspend fun loginPaciente(correo: String, pass: String): PacienteRemoto? {
        return try {
            val resp = apiService.obtenerPacientePorCorreo(getBearer(), correo)
            val p = resp.body()?.items?.firstOrNull()
            if (p != null && p.pass == pass) p else null
        } catch (e: Exception) { null }
    }

    suspend fun obtenerPacientePorId(id: Long) = try { apiService.obtenerPacientePorId(getBearer(), id).body() } catch (e: Exception) { null }

    suspend fun crearPacienteRemoto(req: PacientePostRequest): Boolean {
        return try {
            val resp = apiService.crearPaciente(getBearer(), req)
            if (resp.isSuccessful) {
                req.enfermedades?.forEach { id ->
                    apiService.asociarEnfermedad(getBearer(), PacienteEnfermedadRutPost(req.rut, req.dv, id))
                }
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun registrarEnfermedadesPacientePorRut(rut: Long, dv: String, ids: List<Long>): Boolean {
        return try {
            ids.forEach { id -> apiService.asociarEnfermedad(getBearer(), PacienteEnfermedadRutPost(rut, dv, id)) }
            true
        } catch (e: Exception) { false }
    }

    suspend fun crearDatoSaludRemoto(id: Long, sis: Int?, dias: Int?, glu: Int?, agua: Int?, pasos: Int?): Boolean {
        return try {
            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val body = UserHealthPostRequest(id, fecha, sis, dias, glu, agua, pasos)
            apiService.crearDatoSalud(getBearer(), body).isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun obtenerDesafios() = try { apiService.obtenerDesafios(getBearer()).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
    suspend fun obtenerEjercicios() = try { apiService.obtenerEjercicios(getBearer()).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
    suspend fun obtenerRecetas() = try { apiService.obtenerRecetas(getBearer()).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
    suspend fun obtenerEnfermedades() = try { apiService.obtenerEnfermedades(getBearer()).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
    suspend fun obtenerObjetivos() = try { apiService.obtenerObjetivos(getBearer()).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
    suspend fun obtenerHistorialSalud(id: Long) = try { apiService.obtenerDatosSalud(getBearer(), id).body()?.items.orEmpty() } catch (e: Exception) { emptyList() }
}