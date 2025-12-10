package com.example.wellfit.data.repository

import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.RecetaRemota

class RecetaRepository {
    suspend fun obtenerRecetas(): List<RecetaRemota> {
        return OracleRemoteDataSource.obtenerRecetas()
    }
}