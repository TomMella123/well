package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.SaludDao
import com.example.wellfit.data.local.dto.toDto
import com.example.wellfit.data.local.entities.HistorialPesoEntity
import com.example.wellfit.data.local.entities.UserHealthDataEntity
import com.example.wellfit.data.remote.RetrofitClient

class SaludRepository(
    private val saludDao: SaludDao
) {

    // ===============================
    //  MÉTODOS LOCALES QUE YA TENÍAS
    // ===============================

    // Insertar glucosa, presión, agua, pasos, etc.
    // 👉 Siempre marcamos pendingSync = true para que se pueda enviar luego.
    suspend fun insertHealthData(data: UserHealthDataEntity): Long =
        saludDao.insertUserHealthData(
            if (data.pendingSync) data else data.copy(pendingSync = true)
        )

    // Obtener lista de registros de salud por paciente (local)
    suspend fun getHealthData(idPaciente: Long): List<UserHealthDataEntity> =
        saludDao.getHealthDataByPaciente(idPaciente)

    // Insertar historial de peso
    suspend fun insertHistorialPeso(historial: HistorialPesoEntity): Long =
        saludDao.insertHistorialPeso(
            if (historial.pendingSync) historial else historial.copy(pendingSync = true)
        )

    // Obtener historial de peso
    suspend fun getHistorialPeso(idPaciente: Long): List<HistorialPesoEntity> =
        saludDao.getHistorialPeso(idPaciente)

    // ===============================
    //  MÉTODOS PARA SYNC
    // ===============================

    suspend fun getHealthDataPendienteSync(): List<UserHealthDataEntity> =
        saludDao.getHealthDataPendienteSync()

    suspend fun marcarHealthDataSincronizada(ids: List<Long>) =
        saludDao.marcarHealthDataSincronizada(ids)

    suspend fun getHistorialPesoPendienteSync(): List<HistorialPesoEntity> =
        saludDao.getHistorialPesoPendienteSync()

    suspend fun marcarHistorialPesoSincronizado(ids: List<Long>) =
        saludDao.marcarHistorialPesoSincronizado(ids)

    /**
     * Llamar a este método cuando tengas conexión y quieras
     * enviar TODO lo pendiente al backend.
     *
     * 🔹 Guarda SIEMPRE en local.
     * 🔹 Aquí solo hacemos el "flush" hacia la URL.
     */
    suspend fun syncConServidor() {
        val api = RetrofitClient.api

        // 1) Sync de registros de salud (agua, glucosa, etc.)
        val pendientesHealth = getHealthDataPendienteSync()
        if (pendientesHealth.isNotEmpty()) {
            val dtoList = pendientesHealth.map { it.toDto() }
            val response = api.enviarHealthData(dtoList)

            if (response.isSuccessful) {
                val ids = pendientesHealth.map { it.idData }
                marcarHealthDataSincronizada(ids)
            } else {
                // Aquí puedes loggear o manejar el error si quieres
            }
        }

        // 2) Sync de historial de peso
        val pendientesPeso = getHistorialPesoPendienteSync()
        if (pendientesPeso.isNotEmpty()) {
            val dtoList = pendientesPeso.map { it.toDto() }
            val response = api.enviarHistorialPeso(dtoList)

            if (response.isSuccessful) {
                val ids = pendientesPeso.map { it.idHistorialPeso }
                marcarHistorialPesoSincronizado(ids)
            } else {
                // Manejo de error opcional
            }
        }
    }
}
