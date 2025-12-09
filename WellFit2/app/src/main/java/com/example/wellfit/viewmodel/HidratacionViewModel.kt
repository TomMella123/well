package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.UserHealthDataEntity
import com.example.wellfit.data.repository.SaludRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HidratacionViewModel(
    private val saludRepository: SaludRepository
) : ViewModel() {

    private val _datosHidratacion = MutableStateFlow<List<UserHealthDataEntity>>(emptyList())
    val datosHidratacion: StateFlow<List<UserHealthDataEntity>> = _datosHidratacion

    // ✅ GUARDAR: solo local (Room) + pendingSync = true
    fun registrarAgua(data: UserHealthDataEntity) {
        viewModelScope.launch {
            saludRepository.insertHealthData(data)
            cargarHidratacion(data.idPaciente)
        }
    }

    // ✅ CARGAR: de momento solo local
    fun cargarHidratacion(idPaciente: Long) {
        viewModelScope.launch {
            _datosHidratacion.value = saludRepository.getHealthData(idPaciente)
        }
    }
}
