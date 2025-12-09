package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.RecetaEntity
import com.example.wellfit.data.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecetasViewModel(
    private val recetaRepository: RecetaRepository
) : ViewModel() {

    private val _recetas = MutableStateFlow<List<RecetaEntity>>(emptyList())
    val recetas: StateFlow<List<RecetaEntity>> = _recetas

    fun cargarRecetas() {
        viewModelScope.launch {
            _recetas.value = recetaRepository.getAllRecetas()
        }
    }

    fun cargarRecetasPorEnfermedad(idEnfermedad: Long) {
        viewModelScope.launch {
            _recetas.value = recetaRepository.getRecetasByEnfermedad(idEnfermedad)
        }
    }
}
