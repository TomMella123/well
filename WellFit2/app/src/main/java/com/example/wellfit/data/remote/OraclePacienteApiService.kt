package com.example.wellfit.data.remote

import android.util.Log
import com.example.wellfit.data.local.entities.PacienteEntity
import com.google.gson.annotations.SerializedName
import okhttp3.Credentials
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -------------------------------------------------
// DTOs TOKEN (para oauth/token interno de este archivo)
// -------------------------------------------------

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// -------------------------------------------------
// PACIENTE (GET: viene desde el SELECT de ORDS)
// -------------------------------------------------

data class PacienteRemoto(
    @SerializedName("id_paciente")
    val idPaciente: Long? = null,

    @SerializedName("rut_paciente")
    val rut: Long? = null,

    @SerializedName("dv_paciente")
    val dv: String? = null,

    @SerializedName("nombre_paciente")
    val nombre: String? = null,

    @SerializedName("correo_paciente")
    val correo: String? = null,

    @SerializedName("fecha_nacimiento")
    val fechaNac: String? = null,

    @SerializedName("genero_paciente")
    val genero: String? = null,

    @SerializedName("altura_paciente")
    val altura: Int? = null,

    @SerializedName("peso_actual")
    val peso: Int? = null,

    @SerializedName("id_medico")
    val idMedico: Long? = null,

    @SerializedName("paciente_imageid")
    val pacienteImagen: Long? = null,

    @SerializedName("pass_hash_paciente")
    val pass: String = "",

    @SerializedName("pass_salt_paciente")
    val passSalt: String? = null
)

// Respuesta de colección de ORDS: { "items":[{...},{...}], ... }
data class PacienteListResponse(
    @SerializedName("items")
    val items: List<PacienteRemoto>
)

// -------------------------------------------------
// BODY DEL POST /paciente
// -------------------------------------------------

data class PacientePostRequest(
    val rut: Long,
    val dv: String,
    val nombre: String,
    val email: String,
    @SerializedName("fecha_nac") val fechaNac: String,
    val genero: String,
    val altura: Int,
    val peso: Int,
    @SerializedName("id_medico") val idMedico: Long,
    val password: String,
    @SerializedName("image_id") val imageId: Long? = null,
    val enfermedades: List<Long>? = null
)

data class PacientePostResponse(
    val mensaje: String?
)

// -------------------------------------------------
// ENFERMEDAD
// -------------------------------------------------

data class EnfermedadRemota(
    @SerializedName("id_enfermedad")
    val idEnfermedad: Long,

    @SerializedName("nombre_enfermedad")
    val nombreEnfermedad: String?,

    @SerializedName("descripcion_enfermedad")
    val descripcionEnfermedad: String?
)

data class EnfermedadListResponse(
    @SerializedName("items")
    val items: List<EnfermedadRemota>
)

data class PacienteEnfermedadRutPost(
    val rut: Long,
    val dv: String,
    @SerializedName("id_enfermedad") val idEnfermedad: Long
)

// -------------------------------------------------
// DESAFÍO
// -------------------------------------------------

data class DesafioRemoto(
    @SerializedName("id_desafio")
    val idDesafio: Long,

    @SerializedName("nombre_desafio")
    val nombreDesafio: String?,

    @SerializedName("descripcion_desafio")
    val descripcionDesafio: String?,

    @SerializedName("puntaje")
    val puntaje: Int?,

    @SerializedName("id_dificultad")
    val idDificultad: Int?,

    @SerializedName("desafio_imagen")
    val desafioImagen: String? = null
)

data class DesafioListResponse(
    @SerializedName("items")
    val items: List<DesafioRemoto>
)

// -------------------------------------------------
// OBJETIVO
// -------------------------------------------------

data class ObjetivoRemoto(
    @SerializedName("objetivo_id")
    val idObjetivo: Long,

    @SerializedName("nombre_objetivo")
    val nombreObjetivo: String?,

    @SerializedName("descripcion_objetivo")
    val descripcionObjetivo: String?
)

data class ObjetivoListResponse(
    @SerializedName("items")
    val items: List<ObjetivoRemoto>
)

// -------------------------------------------------
// RELACIÓN OBJ_PAC
// -------------------------------------------------

data class ObjPacRemoto(
    @SerializedName("id_obj_pac")
    val idObjPac: Long? = null,

    @SerializedName("id_paciente")
    val idPaciente: Long,

    @SerializedName("objetivo_id")
    val idObjetivo: Long
)

// -------------------------------------------------
// USER HEALTH DATA (WELLFIT_ADMIN.USER_HEALTH_DATA)
// -------------------------------------------------

data class UserHealthPostRequest(
    val rut: Long,
    val dv: String,
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

data class UserHealthListResponse(
    @SerializedName("items")
    val items: List<UserHealthItem>
)

// =======================
//   Servicios Retrofit
// =======================

interface OracleAuthApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}

interface OraclePacienteApiService {

    // GET /v1/gestion/paciente
    @GET("v1/gestion/paciente")
    suspend fun obtenerPacientes(
        @Header("Authorization") token: String
    ): Response<PacienteListResponse>

    // GET /v1/gestion/paciente/{id}
    @GET("v1/gestion/paciente/{id}")
    suspend fun obtenerPacientePorId(
        @Header("Authorization") token: String,
        @Path("id") idPaciente: Long
    ): Response<PacienteRemoto>

    // GET /v1/gestion/paciente?correo_paciente=...
    @GET("v1/gestion/paciente")
    suspend fun obtenerPacientePorCorreo(
        @Header("Authorization") token: String,
        @Query("correo_paciente") correo: String
    ): Response<PacienteListResponse>

    // POST /v1/gestion/paciente
    @POST("v1/gestion/paciente")
    suspend fun crearPaciente(
        @Header("Authorization") token: String,
        @Body body: PacientePostRequest
    ): Response<PacientePostResponse>
}

interface OracleEnfermedadApiService {

    @GET("v1/gestion/enfermedad")
    suspend fun obtenerEnfermedades(
        @Header("Authorization") token: String
    ): Response<EnfermedadListResponse>

    // Asociar enfermedad a paciente por RUT
    @POST("v1/gestion/enfermedad/pac_enf")
    suspend fun asociarEnfermedadPorRut(
        @Header("Authorization") token: String,
        @Body body: PacienteEnfermedadRutPost
    ): Response<Void>
}

interface OracleDesafioApiService {
    @GET("v1/gestion/desafio")
    suspend fun obtenerDesafios(
        @Header("Authorization") token: String
    ): Response<DesafioListResponse>
}

interface OracleObjetivoApiService {

    @GET("v1/gestion/objetivo")
    suspend fun obtenerObjetivos(
        @Header("Authorization") token: String
    ): Response<ObjetivoListResponse>

    @POST("v1/gestion/obj_pac")
    suspend fun crearObjPac(
        @Header("Authorization") token: String,
        @Body body: ObjPacRemoto
    ): Response<ObjPacRemoto>
}

// NUEVO: servicio para user_health_data
interface OracleUserHealthApiService {

    @POST("v1/gestion/user_health")
    suspend fun crearDatoSalud(
        @Header("Authorization") token: String,
        @Body body: UserHealthPostRequest
    ): Response<Map<String, String>>   // { "mensaje": "Dato de salud creado" }

    @GET("v1/gestion/user_health")
    suspend fun obtenerDatosSalud(
        @Header("Authorization") token: String,
        @Query("rut") rut: Long,
        @Query("dv") dv: String
    ): Response<UserHealthListResponse>
}

// =======================
//   DataSource
// =======================

object OracleRemoteDataSource {

    private const val BASE_URL =
        "https://g653ecd0e488874-wellfit.adb.sa-santiago-1.oraclecloudapps.com/ords/wellfit_admin/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authService: OracleAuthApiService by lazy {
        retrofit.create(OracleAuthApiService::class.java)
    }

    private val pacienteService: OraclePacienteApiService by lazy {
        retrofit.create(OraclePacienteApiService::class.java)
    }

    private val enfermedadService: OracleEnfermedadApiService by lazy {
        retrofit.create(OracleEnfermedadApiService::class.java)
    }

    private val desafioService: OracleDesafioApiService by lazy {
        retrofit.create(OracleDesafioApiService::class.java)
    }

    private val objetivoService: OracleObjetivoApiService by lazy {
        retrofit.create(OracleObjetivoApiService::class.java)
    }

    private val userHealthService: OracleUserHealthApiService by lazy {
        retrofit.create(OracleUserHealthApiService::class.java)
    }

    private suspend fun getBearer(): String {
        val basic = Credentials.basic(
            "3c--eINdA_VFoOTbY5ZEtg..",
            "LEsufeK0VkYOZMP9h5Mmpg.."
        )
        val token = authService.getToken(basic)
        return "Bearer ${token.accessToken}"
    }

    // ------------ LOGIN (REMOTE) -------------
    suspend fun loginPaciente(correo: String, passPlano: String): PacienteRemoto? {
        return try {
            val token = getBearer()
            val resp = pacienteService.obtenerPacientes(token)

            if (!resp.isSuccessful) {
                Log.w(
                    "OracleRemoteDS",
                    "loginPaciente() -> HTTP ${resp.code()} body=${resp.errorBody()?.string()}"
                )
                null
            } else {
                resp.body()
                    ?.items
                    ?.firstOrNull { pac ->
                        pac.correo.equals(correo.trim(), ignoreCase = true) &&
                                pac.pass == passPlano.trim()
                    }
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error loginPaciente", e)
            null
        }
    }

    // ------------ PACIENTE: helpers -------------

    suspend fun obtenerPacientePorId(id: Long): PacienteRemoto? {
        return try {
            val token = getBearer()
            val resp = pacienteService.obtenerPacientePorId(token, id)
            if (resp.isSuccessful) {
                resp.body()
            } else {
                Log.w(
                    "OracleRemoteDS",
                    "obtenerPacientePorId() -> HTTP ${resp.code()} body=${resp.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerPacientePorId", e)
            null
        }
    }

    suspend fun obtenerPacientePorCorreo(correo: String): PacienteRemoto? {
        return try {
            val token = getBearer()
            val resp = pacienteService.obtenerPacientePorCorreo(token, correo)
            if (resp.isSuccessful) {
                resp.body()?.items?.firstOrNull()
            } else {
                Log.w(
                    "OracleRemoteDS",
                    "obtenerPacientePorCorreo() -> HTTP ${resp.code()} body=${resp.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerPacientePorCorreo", e)
            null
        }
    }

    // registrar paciente a partir de la entidad local (Room)
    suspend fun crearPacienteDesdeEntity(paciente: PacienteEntity): PacienteRemoto? {
        // Mapeo básico desde PacienteEntity a PacientePostRequest
        val rutLong = paciente.rutPaciente.toLongOrNull() ?: 0L
        val alturaInt = paciente.alturaPaciente?.toInt() ?: 0
        val pesoInt = paciente.pesoPaciente?.toInt() ?: 0
        val genero = paciente.generoPaciente ?: "Otros"
        val fechaNac = paciente.fechaNacimiento ?: "2000-01-01"
        val idMedico = 30L // fijo por ahora

        val request = PacientePostRequest(
            rut = rutLong,
            dv = paciente.dvPaciente,
            nombre = paciente.nombrePaciente,
            email = paciente.correoPaciente,
            fechaNac = fechaNac,
            genero = genero,
            altura = alturaInt,
            peso = pesoInt,
            idMedico = idMedico,
            password = paciente.passHashPaciente,
            imageId = null,
            enfermedades = null
        )

        val ok = crearPacienteRemoto(request)
        if (!ok) return null

        // Intentamos recuperar el paciente recién creado por correo
        return obtenerPacientePorCorreo(paciente.correoPaciente)
    }

    // ------------ CREAR PACIENTE (desde request directo) -------------
    suspend fun crearPacienteRemoto(request: PacientePostRequest): Boolean {
        return try {
            val token = getBearer()
            val resp = pacienteService.crearPaciente(token, request)
            if (!resp.isSuccessful) {
                val errorText = resp.errorBody()?.string()
                Log.w(
                    "OracleRemoteDS",
                    "crearPacienteRemoto() -> HTTP ${resp.code()} body=$errorText"
                )
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error crearPacienteRemoto", e)
            false
        }
    }

    // ------------ ENFERMEDADES -------------
    suspend fun obtenerEnfermedades(): List<EnfermedadRemota> {
        return try {
            val token = getBearer()
            val resp = enfermedadService.obtenerEnfermedades(token)
            if (resp.isSuccessful) {
                resp.body()?.items.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerEnfermedades", e)
            emptyList()
        }
    }

    suspend fun registrarEnfermedadesPacientePorRut(
        rut: Long,
        dv: String,
        enfermedadesIds: List<Long>
    ): Boolean {
        return try {
            val token = getBearer()
            var okGlobal = true

            for (idEnf in enfermedadesIds) {
                val body = PacienteEnfermedadRutPost(
                    rut = rut,
                    dv = dv.uppercase(),
                    idEnfermedad = idEnf
                )

                val resp = enfermedadService.asociarEnfermedadPorRut(token, body)

                if (resp.isSuccessful) {
                    Log.d(
                        "OracleRemoteDS",
                        "Enfermedad $idEnf asociada por RUT=$rut-${dv.uppercase()} (HTTP ${resp.code()})"
                    )
                } else {
                    okGlobal = false
                    val errorText = resp.errorBody()?.string()
                    Log.w(
                        "OracleRemoteDS",
                        "registrarEnfermedadesPacientePorRut() -> HTTP ${resp.code()} body=$errorText"
                    )
                }
            }
            okGlobal
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error registrarEnfermedadesPacientePorRut", e)
            false
        }
    }

    // ------------ DESAFÍOS -------------
    suspend fun obtenerDesafios(): List<DesafioRemoto> {
        return try {
            val token = getBearer()
            val resp = desafioService.obtenerDesafios(token)
            if (resp.isSuccessful) {
                resp.body()?.items.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerDesafios", e)
            emptyList()
        }
    }

    // ------------ OBJETIVOS -------------
    suspend fun obtenerObjetivos(): List<ObjetivoRemoto> {
        return try {
            val token = getBearer()
            val resp = objetivoService.obtenerObjetivos(token)
            if (resp.isSuccessful) {
                resp.body()?.items.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerObjetivos", e)
            emptyList()
        }
    }

    suspend fun registrarObjetivoPaciente(
        idPacienteRemoto: Long,
        idObjetivo: Long
    ): ObjPacRemoto? {
        return try {
            val token = getBearer()
            val body = ObjPacRemoto(
                idObjPac = null,
                idPaciente = idPacienteRemoto,
                idObjetivo = idObjetivo
            )
            val resp = objetivoService.crearObjPac(token, body)
            if (!resp.isSuccessful) {
                Log.w(
                    "OracleRemoteDS",
                    "registrarObjetivoPaciente() -> HTTP ${resp.code()} body=${resp.errorBody()?.string()}"
                )
                null
            } else {
                resp.body()
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error registrarObjetivoPaciente", e)
            null
        }
    }

    // ------------ DATOS DE SALUD (user_health) -------------
    suspend fun obtenerUserHealthPorRutDv(
        rut: Long,
        dv: String
    ): List<UserHealthItem> {
        return try {
            val token = getBearer()
            val resp = userHealthService.obtenerDatosSalud(token, rut, dv.uppercase())
            if (resp.isSuccessful) {
                resp.body()?.items.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerUserHealthPorRutDv", e)
            emptyList()
        }
    }

    suspend fun obtenerUltimoDatoSalud(
        rut: Long,
        dv: String
    ): UserHealthItem? {
        return try {
            val lista = obtenerUserHealthPorRutDv(rut, dv)
            lista.maxWithOrNull(
                compareBy<UserHealthItem> { it.fechaData }.thenBy { it.idData }
            )
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error obtenerUltimoDatoSalud", e)
            null
        }
    }

    /**
     * Crea un dato de salud remoto.
     * - La fecha se calcula aquí (YYYY-MM-DD).
     * - Sólo rellenas los campos que quieras, el resto van como null.
     */
    suspend fun crearDatoSaludRemoto(
        rut: Long,
        dv: String,
        presionSistolica: Int? = null,
        presionDiastolica: Int? = null,
        glucosaSangre: Int? = null,
        aguaVasos: Int? = null,
        pasos: Int? = null
    ): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "CL"))
            val fechaIso = sdf.format(Date())

            val token = getBearer()
            val body = UserHealthPostRequest(
                rut = rut,
                dv = dv.uppercase(),
                fechaData = fechaIso,
                presionSistolica = presionSistolica,
                presionDiastolica = presionDiastolica,
                glucosaSangre = glucosaSangre,
                aguaVasos = aguaVasos,
                pasos = pasos
            )
            val resp = userHealthService.crearDatoSalud(token, body)
            if (!resp.isSuccessful) {
                Log.w(
                    "OracleRemoteDS",
                    "crearDatoSaludRemoto() -> HTTP ${resp.code()} body=${resp.errorBody()?.string()}"
                )
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e("OracleRemoteDS", "Error crearDatoSaludRemoto", e)
            false
        }
    }
}

/**
 * Helper de nivel superior para que el código viejo
 * pueda llamar simplemente a `crearDatoSaludRemoto(rut, dv, glucosa)`
 * sin preocuparse por la fecha ni los otros campos.
 */
suspend fun crearDatoSaludRemoto(
    rut: Long,
    dv: String,
    glucosa: Int
): Boolean {
    return OracleRemoteDataSource.crearDatoSaludRemoto(
        rut = rut,
        dv = dv,
        glucosaSangre = glucosa
    )
}
