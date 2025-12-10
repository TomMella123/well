package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.PacienteDao
import com.example.wellfit.data.local.entities.PacienteEntity
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacientePostRequest
import com.example.wellfit.data.remote.PacienteRemoto

class PacienteRepository(
    private val pacienteDao: PacienteDao
) {
    // Nota: Ya no pedimos 'apiService' en el constructor porque usamos el DataSource estático.

    // ---------------- LOCAL (ROOM) ----------------
    suspend fun getPacientesLocales(): List<PacienteEntity> {
        return pacienteDao.getAllPacientes()
    }

    suspend fun getPacienteActual(email: String): PacienteEntity? {
        return pacienteDao.getPacientePorCorreo(email)
    }

    suspend fun insertarPacienteLocal(paciente: PacienteEntity) {
        pacienteDao.insertPaciente(paciente)
    }

    suspend fun actualizarPacienteLocal(paciente: PacienteEntity) {
        pacienteDao.updatePaciente(paciente)
    }

    // ---------------- REMOTO (ORACLE CLOUD) ----------------

    // Login: Busca en la nube y devuelve el paciente si la pass coincide
    suspend fun loginRemoto(email: String, pass: String): PacienteRemoto? {
        return OracleRemoteDataSource.loginPaciente(email, pass)
    }

    // Registro: Envía los datos a Oracle
    suspend fun registrarPacienteRemoto(
        rut: Long, dv: String, nombre: String, email: String,
        fechaNac: String, genero: String, altura: Int, peso: Int,
        idMedico: Long, pass: String
    ): Boolean {

        val request = PacientePostRequest(
            rut = rut,
            dv = dv,
            nombre = nombre,
            email = email,
            fechaNac = fechaNac,
            genero = genero,
            altura = altura,
            peso = peso,
            idMedico = idMedico,
            password = pass
        )

        return OracleRemoteDataSource.crearPacienteRemoto(request)
    }
}