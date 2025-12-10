package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val loginPaciente = MutableLiveData<PacienteRemoto?>()
    val registroOk = MutableLiveData<Boolean>()
    val enfermedades = MutableLiveData<List<EnfermedadRemota>>()
    val objetivos = MutableLiveData<List<ObjetivoRemoto>>()
    val enfermedadesOk = MutableLiveData<Boolean?>()

    fun loginRemoto(correo: String, pass: String) {
        viewModelScope.launch {
            // Esto ahora usa la versión con Logs
            val res = OracleRemoteDataSource.loginPaciente(correo, pass)
            loginPaciente.postValue(res)
        }
    }

    fun registrarPacienteRemoto(req: PacientePostRequest) {
        viewModelScope.launch {
            val ok = OracleRemoteDataSource.crearPacienteRemoto(req)
            registroOk.postValue(ok)
        }
    }

    fun cargarEnfermedadesYObjetivos() {
        viewModelScope.launch {
            // Manejamos posibles errores con listas vacías seguras
            try {
                enfermedades.postValue(OracleRemoteDataSource.obtenerEnfermedades())
                objetivos.postValue(OracleRemoteDataSource.obtenerObjetivos())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun asociarEnfermedades(rut: Long, dv: String, ids: List<Long>) {
        viewModelScope.launch {
            val ok = OracleRemoteDataSource.registrarEnfermedadesPacientePorRut(rut, dv, ids)
            enfermedadesOk.postValue(ok)
        }
    }

    fun resetEnfermedadesOk() { enfermedadesOk.value = null }
}