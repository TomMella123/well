package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.OracleRemoteDataSource
import kotlinx.coroutines.launch

class HidratacionViewModel(application: Application) : AndroidViewModel(application) {

    val vasosActuales = MutableLiveData<Int>()

    fun cargarHidratacion(idPaciente: Long) {
        viewModelScope.launch {
            val historial = OracleRemoteDataSource.obtenerHistorialSalud(idPaciente)
            val ultimo = historial.filter { it.aguaVasos != null }.maxByOrNull { it.fechaData + it.idData }
            vasosActuales.postValue(ultimo?.aguaVasos ?: 0)
        }
    }

    fun registrarVaso(idPaciente: Long, cantidad: Int) {
        viewModelScope.launch {
            OracleRemoteDataSource.crearDatoSaludRemoto(idPaciente, null, null, null, cantidad, null)
            vasosActuales.postValue(cantidad)
        }
    }
}