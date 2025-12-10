package com.example.wellfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.ui.salud.PresionItem // NECESARIO para el LiveData de Presión
import com.example.wellfit.ui.hidratacion.AguaItem // NECESARIO para el LiveData de Agua
import kotlinx.coroutines.launch

class SaludViewModel(application: Application) : AndroidViewModel(application) {

    val operacionExitosa = MutableLiveData<Boolean>()
    // LiveData que espera PresionItem (para PresionActivity)
    val historialPresion = MutableLiveData<List<PresionItem>>()
    // LiveData que espera AguaItem (para HidratacionActivity)
    val historialAgua = MutableLiveData<List<AguaItem>>()

    fun registrarDatosSalud(
        idPaciente: Long,
        presionSis: Int? = null,
        presionDias: Int? = null,
        glucosa: Int? = null,
        agua: Int? = null,
        pasos: Int? = null
    ) {
        viewModelScope.launch {
            val exito = OracleRemoteDataSource.crearDatoSaludRemoto(
                idPaciente, presionSis, presionDias, glucosa, agua, pasos
            )
            operacionExitosa.postValue(exito)
        }
    }

    // Función para cargar historial de Presión (usada en PresionActivity.kt)
    fun loadHistorialSalud(idPaciente: Long) {
        // Lógica de carga simulada o real de la base de datos remota
        // NOTA: Esto asume que OracleRemoteDataSource tiene una función para obtener el historial
        // y que lo mapea a List<PresionItem> para la UI.
        viewModelScope.launch {
            // SIMULACIÓN: En un entorno real, la data debe venir de la BDD
            // Aquí se simula la carga vacía.
            // val data = OracleRemoteDataSource.obtenerHistorialPresion(idPaciente)
            historialPresion.postValue(emptyList())
        }
    }

    // Función para cargar historial de Agua (usada en HidratacionActivity.kt)
    fun loadHistorialAgua(idPaciente: Long) {
        // Lógica de carga simulada o real de la base de datos remota
        // NOTA: Esto asume que OracleRemoteDataSource tiene una función para obtener el historial
        // y que lo mapea a List<AguaItem> para la UI.
        viewModelScope.launch {
            // SIMULACIÓN: En un entorno real, la data debe venir de la BDD
            // Aquí se simula la carga vacía.
            // val data = OracleRemoteDataSource.obtenerHistorialAgua(idPaciente)
            historialAgua.postValue(emptyList())
        }
    }
}