package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.DesafioRemoto
import com.example.wellfit.data.repository.DesafioRepository
import kotlinx.coroutines.launch

class DesafiosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DesafioRepository()

    private val _listaDesafios = MutableLiveData<List<DesafioRemoto>>()
    val listaDesafios: LiveData<List<DesafioRemoto>> = _listaDesafios

    init {
        cargarDesafios()
    }

    fun cargarDesafios() {
        viewModelScope.launch {
            val lista = repository.obtenerDesafios()
            _listaDesafios.postValue(lista)
        }
    }
}