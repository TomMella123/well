package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.EjercicioRemoto
import com.example.wellfit.data.repository.EjercicioRepository
import kotlinx.coroutines.launch

class EjercicioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EjercicioRepository()
    private val _ejercicios = MutableLiveData<List<EjercicioRemoto>>()
    val ejercicios: LiveData<List<EjercicioRemoto>> = _ejercicios

    init {
        cargarEjercicios()
    }

    private fun cargarEjercicios() {
        viewModelScope.launch {
            _ejercicios.postValue(repository.obtenerEjercicios())
        }
    }
}