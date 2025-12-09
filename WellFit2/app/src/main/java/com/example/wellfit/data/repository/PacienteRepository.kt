package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.PacienteDao
import com.example.wellfit.data.local.entities.PacienteEntity
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacienteRemoto

class PacienteRepository(
    private val pacienteDao: PacienteDao
) {

    // ---------------- LOCAL (ROOM) ----------------
    suspend fun registrarPacienteLocal(paciente: PacienteEntity): Long =
        pacienteDao.insertPaciente(paciente)

    suspend fun obtenerPacientePorEmail(correo: String): PacienteEntity? =
        pacienteDao.getPacienteByEmail(email = correo)

    // ---------------- REMOTO (ORDS) ----------------
    suspend fun registrarPacienteRemoto(paciente: PacienteEntity): PacienteRemoto? =
        OracleRemoteDataSource.crearPacienteDesdeEntity(paciente)

    suspend fun loginRemoto(correo: String, pass: String): PacienteRemoto? =
        OracleRemoteDataSource.loginPaciente(correo, pass)

    suspend fun obtenerPacienteRemotoPorId(id: Long): PacienteRemoto? =
        OracleRemoteDataSource.obtenerPacientePorId(id)
}
