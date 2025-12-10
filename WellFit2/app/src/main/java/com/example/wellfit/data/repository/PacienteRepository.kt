package com.example.wellfit.data.repository

import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacienteRemoto

class PacienteRepository {

    // Login solo remoto
    suspend fun loginRemoto(correo: String, pass: String): PacienteRemoto? =
        OracleRemoteDataSource.loginPaciente(correo, pass)

    suspend fun obtenerPacienteRemotoPorId(id: Long): PacienteRemoto? =
        OracleRemoteDataSource.obtenerPacientePorId(id)
}