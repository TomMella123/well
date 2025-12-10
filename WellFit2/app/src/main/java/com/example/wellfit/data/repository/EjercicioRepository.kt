package com.example.wellfit.data.repository

import com.example.wellfit.data.remote.EjercicioRemoto
import com.example.wellfit.data.remote.OracleRemoteDataSource

class EjercicioRepository {
    suspend fun obtenerEjercicios(): List<EjercicioRemoto> {
        return OracleRemoteDataSource.obtenerEjercicios()
    }
}