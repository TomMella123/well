package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.local.entities.PacienteEntity
import com.example.wellfit.data.repository.PacienteRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PacienteRepository

    // LiveData para observar el resultado del Login
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    // LiveData para observar el resultado del Registro
    private val _registroResult = MutableLiveData<Boolean>()
    val registroResult: LiveData<Boolean> get() = _registroResult

    // LiveData para errores (opcional)
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        // 1. Obtenemos la base de datos local
        val database = AppDatabase.getDatabase(application)
        val pacienteDao = database.pacienteDao()

        // 2. CORRECCIÓN AQUÍ: Instanciamos el repo SOLO con el DAO
        // (Ya no pasamos ApiService porque el repo usa OracleRemoteDataSource internamente)
        repository = PacienteRepository(pacienteDao)
    }

    // Función de Login
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                // 1. Intentamos login en la Nube (Oracle)
                val pacienteRemoto = repository.loginRemoto(email, pass)

                if (pacienteRemoto != null) {
                    // Login Exitoso en Nube -> Guardamos/Actualizamos en Local para sesión offline
                    val nuevoPacienteLocal = PacienteEntity(
                        rut = pacienteRemoto.rut ?: 0,
                        dv = pacienteRemoto.dv ?: "K",
                        nombre = pacienteRemoto.nombre ?: "Usuario",
                        correo = pacienteRemoto.correo ?: email,
                        fechaNacimiento = pacienteRemoto.fechaNac ?: "",
                        genero = pacienteRemoto.genero ?: "O",
                        altura = pacienteRemoto.altura ?: 0,
                        peso = pacienteRemoto.peso ?: 0,
                        idMedico = pacienteRemoto.idMedico ?: 0,
                        password = pass
                    )
                    // Guardamos en local (Room)
                    repository.insertarPacienteLocal(nuevoPacienteLocal)

                    _loginResult.postValue(true)
                } else {
                    // Si falla en nube, intentamos ver si existe en local (Login Offline)
                    val pacienteLocal = repository.getPacienteActual(email)
                    if (pacienteLocal != null && pacienteLocal.password == pass) {
                        _loginResult.postValue(true)
                    } else {
                        _errorMessage.postValue("Credenciales incorrectas")
                        _loginResult.postValue(false)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error de conexión: ${e.message}")
                _loginResult.postValue(false)
            }
        }
    }

    // Función de Registro
    fun registrar(
        rut: Long, dv: String, nombre: String, email: String,
        fechaNac: String, genero: String, altura: Int, peso: Int,
        idMedico: Long, pass: String
    ) {
        viewModelScope.launch {
            try {
                val exito = repository.registrarPacienteRemoto(
                    rut, dv, nombre, email, fechaNac, genero, altura, peso, idMedico, pass
                )

                if (exito) {
                    // Si se registró en nube, guardamos también copia local
                    val nuevoPaciente = PacienteEntity(
                        rut = rut, dv = dv, nombre = nombre, correo = email,
                        fechaNacimiento = fechaNac, genero = genero, altura = altura,
                        peso = peso, idMedico = idMedico, password = pass
                    )
                    repository.insertarPacienteLocal(nuevoPaciente)
                    _registroResult.postValue(true)
                } else {
                    _errorMessage.postValue("No se pudo registrar en el servidor")
                    _registroResult.postValue(false)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
                _registroResult.postValue(false)
            }
        }
    }
}