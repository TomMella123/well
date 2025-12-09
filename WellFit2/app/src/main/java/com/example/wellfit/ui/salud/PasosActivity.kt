package com.example.wellfit.ui.salud

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.example.wellfit.R
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.local.entities.UserHealthDataEntity
import com.example.wellfit.data.repository.SaludRepository
import com.example.wellfit.viewmodel.SaludViewModel
import kotlinx.coroutines.launch
import java.util.*

class PasosActivity : ComponentActivity() {

    private val metaPasos = 6000

    private lateinit var tvPasosActuales: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvCalorias: TextView
    private lateinit var viewProgress: View

    private lateinit var userPrefs: UserPrefs
    private var idPaciente: Long = 0L

    private val saludViewModel: SaludViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(applicationContext)
                val repo = SaludRepository(db.saludDao())
                return SaludViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasos)

        userPrefs = UserPrefs(this)
        idPaciente = userPrefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        tvPasosActuales = findViewById(R.id.tvPasosActuales)
        tvDistancia = findViewById(R.id.tvDistancia)
        tvCalorias = findViewById(R.id.tvCalorias)
        viewProgress = findViewById(R.id.viewProgressPasos)

        observarHealthData()
        saludViewModel.cargarHealthData(idPaciente)
    }

    private fun observarHealthData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                saludViewModel.healthData.collect { lista ->
                    val registrosPasos = lista.filter {
                        it.idPaciente == idPaciente && it.pasos != null
                    }

                    val pasosHoy = registrosPasos.sumOf { it.pasos ?: 0 }
                    actualizarUI(pasosHoy)
                }
            }
        }
    }

    private fun actualizarUI(pasosHoy: Int) {
        tvPasosActuales.text = "$pasosHoy pasos"

        val distanciaKm = pasosHoy * 0.0008
        val calorias = pasosHoy * 0.04

        tvDistancia.text = String.format(Locale.getDefault(), "%.2f km", distanciaKm)
        tvCalorias.text = String.format(Locale.getDefault(), "%.0f kcal", calorias)

        val ratio = (pasosHoy.toFloat() / metaPasos).coerceIn(0f, 1f)
        val containerWidth =
            resources.displayMetrics.widthPixels - 32 * resources.displayMetrics.density
        viewProgress.layoutParams.width = (containerWidth * ratio).toInt()
        viewProgress.requestLayout()
    }

    fun registrarPasosDesdeReloj(totalPasosHoy: Int) {
        val entidad = UserHealthDataEntity(
            fechaData = "PASOS_HOY", // o DateUtils.today()
            presionSistolica = null,
            presionDiastolica = null,
            glucosaSangre = null,
            aguaVasos = null,
            pasos = totalPasosHoy,
            idPaciente = idPaciente
        )

        saludViewModel.registrarIndicadores(entidad)
    }
}
