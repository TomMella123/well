package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.EnfermedadEntity
import com.example.wellfit.data.local.entities.PacienteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    // ---------------------------------------------------------
    // PACIENTE LOCAL (SIN BD DE MOMENTO)
    // ---------------------------------------------------------
    private val _paciente = MutableStateFlow<PacienteEntity?>(null)
    val paciente: StateFlow<PacienteEntity?> = _paciente

    // Enfermedades asignadas localmente
    private val _enfermedades = MutableStateFlow<List<EnfermedadEntity>>(emptyList())
    val enfermedades: StateFlow<List<EnfermedadEntity>> = _enfermedades

    // ---------------------------------------------------------
    // ENFERMEDADES DISPONIBLES (SOLO 2)
    // ---------------------------------------------------------
    private val enfermedadesBase = listOf(
        EnfermedadEntity(
            idEnfermedad = 1L,
            nombreEnfermedad = "Diabetes",
            descripcionEnfermedad = "Condición metabólica"
        ),
        EnfermedadEntity(
            idEnfermedad = 2L,
            nombreEnfermedad = "Hipertensión",
            descripcionEnfermedad = "Presión arterial elevada"
        )
    )

    private val _enfermedadesDisponibles =
        MutableStateFlow<List<EnfermedadEntity>>(enfermedadesBase)
    val enfermedadesDisponibles: StateFlow<List<EnfermedadEntity>> = _enfermedadesDisponibles

    // ---------------------------------------------------------
    // CARGAR PACIENTE LOCAL (SIN BD)
    // ---------------------------------------------------------
    fun cargarPacienteLocal() {
        _paciente.value = PacienteEntity(
            idPaciente = 1L,
            rutPaciente = "12345678",
            dvPaciente = "K",
            nombrePaciente = "Usuario Ejemplo",
            correoPaciente = "usuario@example.com",

            fechaNacimiento = "1990-01-01",
            generoPaciente = "Otro",

            alturaPaciente = 170.0,
            pesoPaciente = 70.0,

            passHashPaciente = "",
            passSaltPaciente = "",
            pacienteImagenId = null
        )

        _enfermedades.value = emptyList() // sin enfermedades al inicio
    }

    // ---------------------------------------------------------
    // AGREGAR UNA ENFERMEDAD AL PACIENTE
    // ---------------------------------------------------------
    fun asignarEnfermedad(idEnfermedad: Long) {
        viewModelScope.launch {

            val enfermedad = enfermedadesBase.find { it.idEnfermedad == idEnfermedad }
                ?: return@launch

            // evitar duplicados
            if (_enfermedades.value.any { it.idEnfermedad == idEnfermedad }) return@launch

            _enfermedades.value = _enfermedades.value + enfermedad
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR UNA ENFERMEDAD DEL PACIENTE
    // ---------------------------------------------------------
    fun eliminarEnfermedad(idEnfermedad: Long) {
        viewModelScope.launch {
            _enfermedades.value =
                _enfermedades.value.filter { it.idEnfermedad != idEnfermedad }
        }
    }

    // ---------------------------------------------------------
    // ACTUALIZAR DATOS DEL PACIENTE
    // ---------------------------------------------------------
    fun actualizarPerfil(pacienteActualizado: PacienteEntity) {
        _paciente.value = pacienteActualizado
    }
}
