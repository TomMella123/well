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
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// ==========================================
// 1. DTOs / MODELOS REMOTOS
// ==========================================

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class PacienteRemoto(
    @SerializedName("id_paciente") val idPaciente: Long? = null,
    @SerializedName("rut_paciente") val rutPaciente: Long? = null,
    @SerializedName("dv_paciente") val dvPaciente: String? = null,
    @SerializedName("nombre_paciente") val nombre: String? = null,
    @SerializedName("email_paciente") val email: String? = null,
    @SerializedName("fecha_nac_paciente") val fechaNac: String? = null,
    @SerializedName("genero_paciente") val genero: String? = null,
    @SerializedName("altura_paciente") val altura: Int? = null,
    @SerializedName("peso_actual") val pesoActual: Int? = null,
    @SerializedName("id_medico") val idMedico: Long? = null,
    @SerializedName("paciente_imageid") val imageId: String? = null,
    @SerializedName("pass_hash_paciente") val passHash: String? = null,
    @SerializedName("pass_salt_paciente") val passSalt: String? = null
)
data class PacienteListResponse(@SerializedName("items") val items: List<PacienteRemoto>)

data class PacientePostRequest(
    @SerializedName("rut") val rut: Long,
    @SerializedName("dv") val dv: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("fecha_nac") val fechaNac: String,
    @SerializedName("genero") val genero: String,
    @SerializedName("altura") val altura: Int,
    @SerializedName("peso") val pesoActual: Int,
    @SerializedName("id_medico") val idMedico: Long,
    @SerializedName("password") val password: String,
    @SerializedName("enfermedades") val enfermedades: List<Long>? = null,
    @SerializedName("image_id") val imageId: String? = null
)

// Respuesta que incluye el ID del paciente recién creado
data class PacientePostResponse(
    val mensaje: String?,
    @SerializedName("id_paciente") val idPaciente: Long?
)

data class EnfermedadRemota(@SerializedName("id_enfermedad") val idEnfermedad: Long, @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?)
data class EnfermedadListResponse(@SerializedName("items") val items: List<EnfermedadRemota>)

// DTO para asociar usando ID (Enfermedad)
data class PacienteEnfermedadIdPost(
    @SerializedName("id_paciente") val idPaciente: Long,
    @SerializedName("id_enfermedad") val idEnfermedad: Long
)

// DTO para asociar Objetivos (INCLUIDO)
data class PacienteObjetivoPost(
    @SerializedName("id_paciente") val idPaciente: Long,
    @SerializedName("id_objetivo") val idObjetivo: Long
)


data class PacienteEnfermedadRutPost(val rut: Long, val dv: String, @SerializedName("id_enfermedad") val idEnfermedad: Long)

// DTOs para obtener las asociaciones de enfermedades de un paciente
data class AsociacionEnfermedadRemota(
    @SerializedName("id_paciente") val idPaciente: Long,
    @SerializedName("id_enfermedad") val idEnfermedad: Long
)
data class AsociacionEnfermedadListResponse(@SerializedName("items") val items: List<AsociacionEnfermedadRemota>)

// DTOs de Recetas
data class DificultadRemota(@SerializedName("ID_DIFICULTAD") val idDificultad: Int, @SerializedName("NOMBRE_DIFICULTAD") val nombreDificultad: String?)
data class DificultadListResponse(@SerializedName("items") val items: List<DificultadRemota>)

data class RecetaRemota(
    @SerializedName("ID_RECETA") val idReceta: Long,
    @SerializedName("NOMBRE_RECETA") val nombreReceta: String?,
    @SerializedName("DESCRIPCION_RECETA") val descripcionReceta: String?,
    @SerializedName("PASOS_RECETA") val pasosReceta: String?,
    @SerializedName("ID_DIFICULTAD") val idDificultad: Int?,
    @SerializedName("ID_ENFERMEDAD") val idEnfermedad: Long?, // ID de enfermedad asociado
    @SerializedName("RECETA_IMAGEID") val recetaImageId: String?,
    @SerializedName("NOMBRE_ENFERMEDAD") val nombreEnfermedad: String? = null,
    @SerializedName("NOMBRE_DIFICULTAD") val nombreDificultad: String? = null
)
data class RecetaListResponse(@SerializedName("items") val items: List<RecetaRemota>)


// --- OTROS DTOs ---
data class DesafioRemoto(@SerializedName("id_desafio") val idDesafio: Long, @SerializedName("nombre_desafio") val nombreDesafio: String?, @SerializedName("descripcion_desafio") val descripcionDesafio: String?, @SerializedName("puntaje") val puntaje: Int?, @SerializedName("id_dificultad") val idDificultad: Int?)
data class DesafioListResponse(@SerializedName("items") val items: List<DesafioRemoto>)
data class EjercicioRemoto(@SerializedName("id_ejercicio") val idEjercicio: Long, @SerializedName("nombre_ejercicio") val nombreEjercicio: String?, @SerializedName("descripcion_ejercicio") val descripcionEjercicio: String?, @SerializedName("series") val series: Int?, @SerializedName("repeticiones") val repeticiones: Int?)
data class EjercicioListResponse(@SerializedName("items") val items: List<EjercicioRemoto>)
data class ObjetivoRemoto(
    @SerializedName("objetivo_id") val idObjetivo: Long,
    // Mapeamos la descripción al campo de nombre para que se muestre en la UI
    @SerializedName("descripcion_objetivo") val nombreObjetivo: String?
)
data class ObjetivoListResponse(@SerializedName("items") val items: List<ObjetivoRemoto>)

// --- DTOs SALUD ---
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

// ==========================================
// 2. INTERFACES API
// ==========================================

interface OracleAuthApiService {
    @FormUrlEncoded
    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/oauth/token")
    suspend fun getToken(@Header("Authorization") auth: String, @Field("grant_type") grant: String = "client_credentials"): TokenResponse
}

interface OracleApiService {
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/paciente")
    suspend fun obtenerPacientePorEmail(@Header("Authorization") token: String, @Query("email_paciente") email: String): Response<PacienteListResponse>

    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/paciente")
    suspend fun crearPaciente(@Header("Authorization") token: String, @Body body: PacientePostRequest): Response<PacientePostResponse>

    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/paciente/{id}")
    suspend fun obtenerPacientePorId(@Header("Authorization") token: String, @Path("id") id: Long): Response<PacienteRemoto>

    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/enfermedad")
    suspend fun obtenerEnfermedades(@Header("Authorization") token: String): Response<EnfermedadListResponse>

    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/paciente_enfermedad")
    suspend fun obtenerAsociacionesEnfermedadPorPaciente(@Header("Authorization") token: String, @Query("id_paciente") id: Long): Response<AsociacionEnfermedadListResponse>

    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/enfermedad")
    suspend fun asociarEnfermedadPorId(@Header("Authorization") token: String, @Body body: PacienteEnfermedadIdPost): Response<Unit>

    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/enfermedad/pac_enf")
    suspend fun asociarEnfermedad(@Header("Authorization") token: String, @Body body: PacienteEnfermedadRutPost): Response<Unit>

    // --- Recetas y Dificultades ---
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/dificultad")
    suspend fun obtenerDificultades(@Header("Authorization") token: String): Response<DificultadListResponse>

    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/receta")
    suspend fun obtenerRecetas(
        @Header("Authorization") token: String,
        @Query("id_dificultad") idDificultad: Int? = null
    ): Response<RecetaListResponse>

    // Otros Endpoints
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/desafio") suspend fun obtenerDesafios(@Header("Authorization") token: String): Response<DesafioListResponse>
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/ejercicio") suspend fun obtenerEjercicios(@Header("Authorization") token: String): Response<EjercicioListResponse>
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/receta") suspend fun obtenerRecetasLegacy(@Header("Authorization") token: String): Response<RecetaListResponse>
    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/objetivo") suspend fun obtenerObjetivos(@Header("Authorization") token: String): Response<ObjetivoListResponse>

    // --- ENDPOINT PARA ASOCIAR OBJETIVOS (CORRECCIÓN FINAL) ---
    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/objetivo")
    suspend fun asociarObjetivo(@Header("Authorization") token: String, @Body body: PacienteObjetivoPost): Response<Unit>

    // --- SALUD ---
    @POST("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/user_health")
    suspend fun crearDatoSalud(@Header("Authorization") token: String, @Body body: UserHealthPostRequest): Response<Map<String, String>>

    @GET("https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/v1/gestion/user_health")
    suspend fun obtenerDatosSalud(@Header("Authorization") token: String, @Query("id_paciente") id: Long): Response<UserHealthListResponse>
}

// ==========================================
// 3. DATASOURCE CENTRALIZADO
// ==========================================

object OracleRemoteDataSource {
    private const val BASE_URL = "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/"

    private val logging = HttpLoggingInterceptor { msg -> Log.d("OracleHTTP", msg) }
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
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
            accessToken = resp.accessToken
            expiresAt = now + resp.expiresIn
            "Bearer ${resp.accessToken}"
        } catch (e: Exception) {
            Log.e("OracleAuth", "Error Token: ${e.message}")
            ""
        }
    }

    // --- LOGIN ---
    suspend fun loginPaciente(email: String, passInput: String): PacienteRemoto? {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return null

            val resp = apiService.obtenerPacientePorEmail(token, email)
            if (resp.isSuccessful) {
                val items = resp.body()?.items.orEmpty()
                val paciente = items.find { it.email.equals(email, ignoreCase = true) }

                if (paciente != null) {
                    val salt = paciente.passSalt ?: ""
                    val hashDB = paciente.passHash ?: ""

                    if (salt == "PLAIN") {
                        if (passInput == hashDB) return paciente
                        else Log.e("OracleLogin", "Password plana incorrecta")
                    } else if (salt.isNotEmpty() && hashDB.isNotEmpty()) {
                        val hashApp = calcularHashLocal(passInput, salt)
                        if (hashApp == hashDB) return paciente
                        else Log.e("OracleLogin", "Password hash incorrecta")
                    } else {
                        if (passInput == hashDB) return paciente
                        Log.e("OracleLogin", "Credenciales inválidas")
                    }
                } else {
                    Log.e("OracleLogin", "Usuario no encontrado en la respuesta.")
                }
            }
            null
        } catch (e: Exception) { null }
    }

    private fun calcularHashLocal(password: String, salt: String): String {
        return try {
            val input = password + salt
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02X".format(it) }
        } catch (e: Exception) { "" }
    }

    // --- REGISTRO ---
    suspend fun crearPacienteRemoto(req: PacientePostRequest): Boolean {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return false

            val resp = apiService.crearPaciente(token, req)
            if (resp.isSuccessful) {
                val nuevoId = resp.body()?.idPaciente
                Log.d("OracleRegister", "Paciente creado con ID: $nuevoId")

                if (nuevoId != null && !req.enfermedades.isNullOrEmpty()) {
                    Log.d("OracleRegister", "Asociando ${req.enfermedades.size} enfermedades...")
                    req.enfermedades.forEach { idEnf ->
                        val r = apiService.asociarEnfermedadPorId(
                            token,
                            PacienteEnfermedadIdPost(nuevoId, idEnf)
                        )
                        if (!r.isSuccessful) Log.e("OracleRegister", "Fallo asociar enfermedad $idEnf: ${r.code()}")
                    }
                }
                true
            } else {
                Log.e("OracleRegister", "Error crear paciente: ${resp.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("OracleRegister", "Excepción: ${e.message}")
            false
        }
    }

    // --- ASOCIACIÓN DE OBJETIVOS ---
    suspend fun asociarObjetivoPaciente(idPaciente: Long, idObjetivo: Long): Boolean {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return false

            val body = PacienteObjetivoPost(idPaciente, idObjetivo)

            // Llama al endpoint corregido
            val resp = apiService.asociarObjetivo(token, body)

            // 201 Created o 200 OK (si ya existía, según la lógica SQL)
            if (resp.isSuccessful || resp.code() == 200) {
                return true
            } else {
                Log.e("OracleObjective", "Fallo al asociar objetivo: ${resp.code()}")
                return false
            }
        } catch (e: Exception) {
            Log.e("OracleObjective", "Excepción al asociar objetivo: ${e.message}")
            false
        }
    }

    // --- SALUD ---
    suspend fun crearDatoSaludRemoto(id: Long, sis: Int?, dias: Int?, glu: Int?, agua: Int?, pasos: Int?): Boolean {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return false

            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val body = UserHealthPostRequest(
                idPaciente = id,
                fechaData = fecha,
                presionSistolica = sis,
                presionDiastolica = dias,
                glucosaSangre = glu,
                aguaVasos = agua,
                pasos = pasos
            )

            Log.d("OracleHealth", "Enviando salud: $body")
            val resp = apiService.crearDatoSalud(token, body)
            resp.isSuccessful
        } catch (e: Exception) {
            Log.e("OracleHealth", "Error enviando salud: ${e.message}")
            false
        }
    }

    suspend fun obtenerHistorialSalud(id: Long): List<UserHealthItem> {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return emptyList()

            val resp = apiService.obtenerDatosSalud(token, id)
            resp.body()?.items.orEmpty()
        } catch (e: Exception) { emptyList() }
    }

    // --- PERFIL Y ENFERMEDADES ---

    // Método de respaldo para obtener el perfil.
    suspend fun obtenerPacientePorEmail(email: String): PacienteRemoto? {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return null

            // Usamos la ruta funcional de búsqueda por email
            val resp = apiService.obtenerPacientePorEmail(token, email)
            resp.body()?.items?.firstOrNull()
        } catch(e: Exception) {
            Log.e("OracleProfile", "Error al obtener paciente por email: ${e.message}")
            null
        }
    }

    // Función original de obtener por ID (que falla 404, pero se mantiene la firma)
    suspend fun obtenerPacientePorId(id: Long): PacienteRemoto? {
        return try { apiService.obtenerPacientePorId(getBearer(), id).body() } catch(e: Exception) { null }
    }

    // Devuelve una lista de las asociaciones (ID paciente y ID enfermedad) para ese paciente
    suspend fun obtenerAsociacionesEnfermedadPaciente(id: Long): List<AsociacionEnfermedadRemota> {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return emptyList()

            val resp = apiService.obtenerAsociacionesEnfermedadPorPaciente(token, id)
            resp.body()?.items.orEmpty()
        } catch (e: Exception) {
            Log.e("OracleEnf", "Error al obtener asociaciones de enfermedad: ${e.message}")
            emptyList()
        }
    }

    // Obtiene el catálogo de todas las enfermedades
    suspend fun obtenerEnfermedades() = safeCall { apiService.obtenerEnfermedades(getBearer()).body()?.items.orEmpty() }

    // --- RECETAS ---
    /**
     * Obtiene todas las recetas, opcionalmente filtradas por idDificultad en la llamada a la API.
     */
    suspend fun obtenerRecetas(idDificultad: Int? = null): List<RecetaRemota> {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return emptyList()

            val resp = apiService.obtenerRecetas(token, idDificultad)
            resp.body()?.items.orEmpty()
        } catch (e: Exception) {
            Log.e("OracleRecetas", "Error al obtener recetas: ${e.message}")
            emptyList()
        }
    }

    /**
     * Obtiene el catálogo de dificultades para el filtro de la UI.
     */
    suspend fun obtenerDificultades(): List<DificultadRemota> {
        return try {
            val token = getBearer()
            if (token.isEmpty()) return emptyList()
            val resp = apiService.obtenerDificultades(token)
            resp.body()?.items.orEmpty()
        } catch (e: Exception) {
            Log.e("OracleDificultades", "Error al obtener dificultades: ${e.message}")
            emptyList()
        }
    }


    // --- OTROS MÉTODOS AUXILIARES ---
    suspend fun obtenerObjetivos() = safeCall { apiService.obtenerObjetivos(getBearer()).body()?.items.orEmpty() }
    suspend fun obtenerDesafios() = safeCall { apiService.obtenerDesafios(getBearer()).body()?.items.orEmpty() }
    suspend fun obtenerEjercicios() = safeCall { apiService.obtenerEjercicios(getBearer()).body()?.items.orEmpty() }
    suspend fun obtenerRecetasLegacy() = safeCall { apiService.obtenerRecetasLegacy(getBearer()).body()?.items.orEmpty() }
    suspend fun registrarEnfermedadesPacientePorRut(rut: Long, dv: String, ids: List<Long>) = false // Deprecated

    private suspend fun <T> safeCall(action: suspend () -> T): T {
        return try { action() } catch (e: Exception) { throw e }
    }
}