package com.example.wellfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.local.entities.DesafioEntity
import com.example.wellfit.data.local.entities.ObjetivoEntity
import com.example.wellfit.data.repository.DesafioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DesafiosViewModel(
    private val desafioRepository: DesafioRepository
) : ViewModel() {

    private val _desafios = MutableStateFlow<List<DesafioEntity>>(emptyList())
    val desafios: StateFlow<List<DesafioEntity>> = _desafios

    private val _objetivos = MutableStateFlow<List<ObjetivoEntity>>(emptyList())
    val objetivos: StateFlow<List<ObjetivoEntity>> = _objetivos

    fun cargarDesafios() {
        viewModelScope.launch {
            _desafios.value = desafioRepository.getAllDesafios()
        }
    }

    fun cargarObjetivos(idPaciente: Long) {
        viewModelScope.launch {
            _objetivos.value = desafioRepository.getObjetivosByPaciente(idPaciente)
        }
    }
}
