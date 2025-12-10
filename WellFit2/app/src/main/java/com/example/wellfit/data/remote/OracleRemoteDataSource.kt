package com.example.wellfit.data.remote

import android.util.Log
import com.example.wellfit.data.remote.oracle.OracleAuthManager
import com.example.wellfit.data.remote.oracle.OracleRetrofitClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OracleRemoteDataSource {

    // Usamos el cliente único configurado
    private val pacienteService = OracleRetrofitClient.pacienteService
    private val enfermedadService = OracleRetrofitClient.enfermedadService
    private val desafioService = OracleRetrofitClient.desafioService
    private val objetivoService = OracleRetrofitClient.objetivoService
    private val userHealthService = OracleRetrofitClient.userHealthService

    // Helper para obtener el token automáticamente
    private suspend fun getBearer(): String {
        return OracleAuthManager.getBearerHeader()
    }

    // ==========================================
    // LOGIN (Busca por correo)
    // ==========================================
    suspend fun loginPaciente(correo: String, passPlano: String): PacienteRemoto? {
        return try {
            val token = getBearer()
            // Buscamos al usuario en Oracle por su correo
            val resp = pacienteService.obtenerPacientePorCorreo(token, correo)

            if (resp.isSuccessful) {
                val lista = resp.body()?.items
                val paciente = lista?.firstOrNull()

                // Verificamos contraseña (simple)
                if (paciente != null && paciente.pass == passPlano) {
                    return paciente
                }
            } else {
                Log.e("OracleDS", "Error login: ${resp.code()} - ${resp.errorBody()?.string()}")
            }
            null
        } catch (e: Exception) {
            Log.e("OracleDS", "Excepción en Login", e)
            null
        }
    }

    // ==========================================
    // REGISTRO
    // ==========================================
    suspend fun crearPacienteRemoto(request: PacientePostRequest): Boolean {
        return try {
            val token = getBearer()
            val resp = pacienteService.crearPaciente(token, request)

            if (resp.isSuccessful) {
                // Si el registro básico funcionó y hay enfermedades seleccionadas, las asociamos
                if (!request.enfermedades.isNullOrEmpty()) {
                    registrarEnfermedadesPacientePorRut(request.rut, request.dv, request.enfermedades)
                }
                true
            } else {
                Log.e("OracleDS", "Error crearPaciente: ${resp.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("OracleDS", "Excepción crearPaciente", e)
            false
        }
    }

    // ==========================================
    // ENFERMEDADES (Relación Muchos a Muchos)
    // ==========================================
    suspend fun obtenerEnfermedades(): List<EnfermedadRemota> {
        return try {
            val resp = enfermedadService.obtenerEnfermedades(getBearer())
            if (resp.isSuccessful) resp.body()?.items.orEmpty() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private suspend fun registrarEnfermedadesPacientePorRut(
        rut: Long,
        dv: String,
        enfermedadesIds: List<Long>
    ) {
        try {
            val token = getBearer()
            for (idEnf in enfermedadesIds) {
                // Ajusta este body según tu API de relación
                val body = PacienteEnfermedadRutPost(rut, dv.uppercase(), idEnf)
                enfermedadService.asociarEnfermedadPorRut(token, body)
            }
        } catch (e: Exception) {
            Log.e("OracleDS", "Error asociando enfermedades", e)
        }
    }

    // ==========================================
    // SALUD Y OTROS
    // ==========================================
    suspend fun crearDatoSaludRemoto(
        rut: Long, dv: String,
        presionSis: Int?, presionDias: Int?,
        glucosa: Int?, pasos: Int?, agua: Int?
    ): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "CL"))
            val fechaIso = sdf.format(Date())

            val body = UserHealthPostRequest(
                rut = rut, dv = dv, fechaData = fechaIso,
                presionSistolica = presionSis, presionDiastolica = presionDias,
                glucosaSangre = glucosa, pasos = pasos, aguaVasos = agua
            )
            val resp = userHealthService.crearDatoSalud(getBearer(), body)
            resp.isSuccessful
        } catch (e: Exception) { false }
    }
}