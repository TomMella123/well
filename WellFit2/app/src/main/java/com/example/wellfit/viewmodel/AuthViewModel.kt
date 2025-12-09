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

    // --- LOGIN ---
    private val _loginPaciente = MutableLiveData<PacienteRemoto?>()
    val loginPaciente: LiveData<PacienteRemoto?> = _loginPaciente

    // --- REGISTRO ---
    private val _registroOk = MutableLiveData<Boolean>()
    val registroOk: LiveData<Boolean> = _registroOk

    // --- ENFERMEDADES / OBJETIVOS ---
    private val _enfermedades = MutableLiveData<List<EnfermedadRemota>>()
    val enfermedades: LiveData<List<EnfermedadRemota>> = _enfermedades

    private val _objetivos = MutableLiveData<List<ObjetivoRemoto>>()
    val objetivos: LiveData<List<ObjetivoRemoto>> = _objetivos

    // Resultado de asociar enfermedades al paciente
    private val _enfermedadesOk = MutableLiveData<Boolean?>()
    val enfermedadesOk: LiveData<Boolean?> = _enfermedadesOk

    // --- DATO DE SALUD (POST user_health) ---
    private val _datoSaludOk = MutableLiveData<Boolean>()
    val datoSaludOk: LiveData<Boolean> = _datoSaludOk

    // --------------------------------------------------------------------
    // LOGIN: usa el endpoint remoto que tú definas en OracleRemoteDataSource
    // --------------------------------------------------------------------
    fun loginRemoto(correo: String, password: String) {
        viewModelScope.launch {
            try {
                val paciente = OracleRemoteDataSource.loginPaciente(correo, password)
                _loginPaciente.value = paciente
            } catch (e: Exception) {
                Log.e("AuthVM", "Error en loginRemoto", e)
                _loginPaciente.value = null
            }
        }
    }

    // --------------------------------------------------------------------
    // REGISTRAR PACIENTE REMOTO (POST /v1/gestion/paciente)
    // --------------------------------------------------------------------
    fun registrarPacienteRemoto(request: PacientePostRequest) {
        viewModelScope.launch {
            try {
                val ok = OracleRemoteDataSource.crearPacienteRemoto(request)
                _registroOk.value = ok
            } catch (e: Exception) {
                Log.e("AuthVM", "Error registrarPacienteRemoto", e)
                _registroOk.value = false
            }
        }
    }

    // --------------------------------------------------------------------
    // ENFERMEDADES + OBJETIVOS (catálogos)
    // --------------------------------------------------------------------
    fun cargarEnfermedadesYObjetivos() {
        viewModelScope.launch {
            try {
                val enf = OracleRemoteDataSource.obtenerEnfermedades()
                val obj = OracleRemoteDataSource.obtenerObjetivos()
                _enfermedades.value = enf
                _objetivos.value = obj
            } catch (e: Exception) {
                Log.e("AuthVM", "Error cargarEnfermedadesYObjetivos", e)
                _enfermedades.value = emptyList()
                _objetivos.value = emptyList()
            }
        }
    }

    /**
     * Asocia una o varias enfermedades al paciente vía /v1/gestion/enfermedad/pac_enf,
     * usando el RUT + DV. Internamente hace un POST por cada id_enfermedad.
     */
    fun asociarEnfermedades(
        rut: Long,
        dv: String,
        enfermedadesIds: List<Long>
    ) {
        viewModelScope.launch {
            try {
                val ok = OracleRemoteDataSource.registrarEnfermedadesPacientePorRut(
                    rut = rut,
                    dv = dv,
                    enfermedadesIds = enfermedadesIds
                )
                _enfermedadesOk.value = ok
            } catch (e: Exception) {
                Log.e("AuthVM", "Error asociarEnfermedades", e)
                _enfermedadesOk.value = false
            }
        }
    }

    fun resetEnfermedadesOk() {
        _enfermedadesOk.value = null
    }

    // --------------------------------------------------------------------
    // USER_HEALTH_DATA (POST remoto)
    // --------------------------------------------------------------------
    /**
     * Envía un dato de salud remoto a /v1/gestion/user_health.
     * Los campos que no uses los pasas como null.
     */
    fun crearDatoSaludRemoto(
        rut: Long,
        dv: String,
        presionSistolica: Int?,
        presionDiastolica: Int?,
        glucosaSangre: Int?,
        aguaVasos: Int?,
        pasos: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val ok = OracleRemoteDataSource.crearDatoSaludRemoto(
                    rut = rut,
                    dv = dv,
                    presionSistolica = presionSistolica,
                    presionDiastolica = presionDiastolica,
                    glucosaSangre = glucosaSangre,
                    aguaVasos = aguaVasos,
                    pasos = pasos
                )
                _datoSaludOk.value = ok
            } catch (e: Exception) {
                Log.e("AuthVM", "Error crearDatoSaludRemoto", e)
                _datoSaludOk.value = false
            }
        }
    }
}
