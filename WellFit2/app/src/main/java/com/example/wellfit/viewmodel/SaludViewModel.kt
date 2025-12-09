package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.HistorialPesoEntity
import com.example.wellfit.data.local.entities.UserHealthDataEntity
import com.example.wellfit.data.repository.SaludRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SaludViewModel(
    private val saludRepository: SaludRepository
) : ViewModel() {

    private val _healthData = MutableStateFlow<List<UserHealthDataEntity>>(emptyList())
    val healthData: StateFlow<List<UserHealthDataEntity>> = _healthData

    private val _historialPeso = MutableStateFlow<List<HistorialPesoEntity>>(emptyList())
    val historialPeso: StateFlow<List<HistorialPesoEntity>> = _historialPeso

    fun registrarIndicadores(data: UserHealthDataEntity) {
        viewModelScope.launch {
            saludRepository.insertHealthData(data)
            cargarHealthData(data.idPaciente)
        }
    }

    fun registrarPeso(historial: HistorialPesoEntity) {
        viewModelScope.launch {
            saludRepository.insertHistorialPeso(historial)
            cargarHistorialPeso(historial.idPaciente)
        }
    }

    fun cargarHealthData(idPaciente: Long) {
        viewModelScope.launch {
            _healthData.value = saludRepository.getHealthData(idPaciente)
        }
    }

    fun cargarHistorialPeso(idPaciente: Long) {
        viewModelScope.launch {
            _historialPeso.value = saludRepository.getHistorialPeso(idPaciente)
        }
    }
}
