package com.example.wellfit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.EnfermedadRemota
import com.example.wellfit.data.remote.ObjetivoRemoto
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacientePostRequest
import com.example.wellfit.data.remote.PacienteRemoto
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginPaciente = MutableLiveData<PacienteRemoto?>()
    val loginPaciente: LiveData<PacienteRemoto?> = _loginPaciente

    private val _registroOk = MutableLiveData<Boolean>()
    val registroOk: LiveData<Boolean> = _registroOk

    private val _enfermedades = MutableLiveData<List<EnfermedadRemota>>()
    val enfermedades: LiveData<List<EnfermedadRemota>> = _enfermedades

    private val _objetivos = MutableLiveData<List<ObjetivoRemoto>>()
    val objetivos: LiveData<List<ObjetivoRemoto>> = _objetivos

    private val _enfermedadesOk = MutableLiveData<Boolean?>()
    val enfermedadesOk: LiveData<Boolean?> = _enfermedadesOk

    // LOGIN (Sin guardar en local)
    fun loginRemoto(correo: String, password: String) {
        viewModelScope.launch {
            try {
                val paciente = OracleRemoteDataSource.loginPaciente(correo, password)
                _loginPaciente.postValue(paciente)
            } catch (e: Exception) {
                Log.e("AuthVM", "Error login", e)
                _loginPaciente.postValue(null)
            }
        }
    }

    // REGISTRO (Sin guardar en local)
    fun registrarPacienteRemoto(request: PacientePostRequest) {
        viewModelScope.launch {
            try {
                val ok = OracleRemoteDataSource.crearPacienteRemoto(request)
                _registroOk.postValue(ok)
            } catch (e: Exception) {
                _registroOk.postValue(false)
            }
        }
    }

    fun cargarEnfermedadesYObjetivos() {
        viewModelScope.launch {
            _enfermedades.postValue(OracleRemoteDataSource.obtenerEnfermedades())
            _objetivos.postValue(OracleRemoteDataSource.obtenerObjetivos())
        }
    }

    fun asociarEnfermedades(rut: Long, dv: String, enfermedadesIds: List<Long>) {
        viewModelScope.launch {
            val ok = OracleRemoteDataSource.registrarEnfermedadesPacientePorRut(rut, dv, enfermedadesIds)
            _enfermedadesOk.postValue(ok)
        }
    }

    fun resetEnfermedadesOk() {
        _enfermedadesOk.value = null
    }
}