package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.DificultadEntity
import com.example.wellfit.data.local.entities.EjercicioEntity
import com.example.wellfit.data.repository.EjercicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EjercicioViewModel(
    private val ejercicioRepository: EjercicioRepository
) : ViewModel() {

    private val _ejercicios = MutableStateFlow<List<EjercicioEntity>>(emptyList())
    val ejercicios: StateFlow<List<EjercicioEntity>> = _ejercicios

    private val _dificultades = MutableStateFlow<List<DificultadEntity>>(emptyList())
    val dificultades: StateFlow<List<DificultadEntity>> = _dificultades

    fun cargarEjercicios() {
        viewModelScope.launch {
            _ejercicios.value = ejercicioRepository.getEjercicios()
        }
    }

    fun cargarDificultades() {
        viewModelScope.launch {
            _dificultades.value = ejercicioRepository.getDificultades()
        }
    }
}
