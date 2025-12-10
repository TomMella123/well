package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.EjercicioRemoto
import com.example.wellfit.data.repository.EjercicioRepository
import kotlinx.coroutines.launch

class EjercicioViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EjercicioRepository()
    val ejercicios = MutableLiveData<List<EjercicioRemoto>>()
    init {
        viewModelScope.launch { ejercicios.postValue(repo.obtenerEjercicios()) }
    }
}