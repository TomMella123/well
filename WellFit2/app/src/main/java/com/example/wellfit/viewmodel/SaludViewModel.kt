package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.repository.SaludRepository
import kotlinx.coroutines.launch

class SaludViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaludRepository()

    private val _operacionExitosa = MutableLiveData<Boolean>()
    val operacionExitosa: LiveData<Boolean> = _operacionExitosa

    // Llama a esto desde tus Activities (GlucosaActivity, PresionActivity)
    // Pasando el ID del paciente logueado (NO el RUT)
    fun registrarDatosSalud(
        idPaciente: Long,
        presionSis: Int? = null,
        presionDias: Int? = null,
        glucosa: Int? = null,
        agua: Int? = null,
        pasos: Int? = null
    ) {
        viewModelScope.launch {
            val exito = repository.subirDatosSalud(idPaciente, presionSis, presionDias, glucosa, agua, pasos)
            _operacionExitosa.postValue(exito)
        }
    }
}