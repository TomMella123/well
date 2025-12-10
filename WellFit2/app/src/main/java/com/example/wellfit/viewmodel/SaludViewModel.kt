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

    // ESTA ES LA VARIABLE QUE FALTABA O ESTABA MAL ESCRITA
    private val _operacionExitosa = MutableLiveData<Boolean>()
    val operacionExitosa: LiveData<Boolean> get() = _operacionExitosa

    fun registrarDatosSalud(
        idPaciente: Long,
        presionSis: Int? = null,
        presionDias: Int? = null,
        glucosa: Int? = null,
        agua: Int? = null,
        pasos: Int? = null
    ) {
        viewModelScope.launch {
            // Llamamos al repositorio
            val exito = repository.subirDatosSalud(
                idPaciente, presionSis, presionDias, glucosa, agua, pasos
            )
            // Notificamos a la vista
            _operacionExitosa.postValue(exito)
        }
    }
}