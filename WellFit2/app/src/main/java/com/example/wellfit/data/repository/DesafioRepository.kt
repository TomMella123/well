package com.example.wellfit.data.repository
import com.example.wellfit.data.remote.OracleRemoteDataSource
class DesafioRepository {
    suspend fun obtenerDesafios() = OracleRemoteDataSource.obtenerDesafios()
}