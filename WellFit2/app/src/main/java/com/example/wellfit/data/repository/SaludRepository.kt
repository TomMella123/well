package com.example.wellfit.data.repository

import com.example.wellfit.data.remote.OracleRemoteDataSource

class SaludRepository {
    // Solo llama al remoto
    suspend fun subirDatosSalud(
        idPaciente: Long,
        presionSis: Int?, presionDias: Int?,
        glucosa: Int?, agua: Int?, pasos: Int?
    ): Boolean {
        return OracleRemoteDataSource.registrarSalud(
            idPaciente, presionSis, presionDias, glucosa, agua, pasos
        )
    }
}