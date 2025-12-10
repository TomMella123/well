package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.RecetaRemota
import com.example.wellfit.data.repository.RecetaRepository
import kotlinx.coroutines.launch

class RecetasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecetaRepository()
    private val _listaRecetas = MutableLiveData<List<RecetaRemota>>()
    val listaRecetas: LiveData<List<RecetaRemota>> = _listaRecetas

    init {
        cargarRecetas()
    }

    fun cargarRecetas() {
        viewModelScope.launch {
            _listaRecetas.postValue(repository.obtenerRecetas())
        }
    }
}