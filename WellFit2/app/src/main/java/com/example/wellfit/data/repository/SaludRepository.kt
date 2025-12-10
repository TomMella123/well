package com.example.wellfit.data.repository

import com.example.wellfit.data.remote.OracleRemoteDataSource
class SaludRepository {
    suspend fun subirDatosSalud(id: Long, sis: Int?, dias: Int?, glu: Int?, agua: Int?, pasos: Int?) =
        OracleRemoteDataSource.crearDatoSaludRemoto(id, sis, dias, glu, agua, pasos)
}