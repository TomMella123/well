package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.DesafioRemoto
import com.example.wellfit.data.remote.OracleRemoteDataSource
import kotlinx.coroutines.launch

class DesafiosViewModel(app: Application) : AndroidViewModel(app) {
    val listaDesafios = MutableLiveData<List<DesafioRemoto>>()
    init {
        viewModelScope.launch { listaDesafios.postValue(OracleRemoteDataSource.obtenerDesafios()) }
    }
}