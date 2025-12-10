package com.example.wellfit.ui.salud

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.databinding.ActivityPasosBinding
import com.example.wellfit.viewmodel.SaludViewModel
import kotlinx.coroutines.launch

class PasosActivity : BaseActivity() {

    private lateinit var binding: ActivityPasosBinding
    // El ViewModel se instancia automáticamente con la fábrica por defecto
    private val viewModel: SaludViewModel by viewModels()
    private var idPaciente: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Obtener ID real del usuario
        val prefs = UserPrefs(this)
        val idString = prefs.getString("idPaciente")
        idPaciente = idString?.toLongOrNull() ?: 0L

        // 2. Cargar datos
        cargarPasosRemotos()

        // 3. Configurar botón de volver (Verifica si tienes este ID en tu XML, si no, comenta esta línea)
        val btnBack = findViewById<View>(R.id.imgHeaderEjercicio) // O el ID que uses para volver
        btnBack?.setOnClickListener { finish() }
    }

    private fun cargarPasosRemotos() {
        if (idPaciente == 0L) return

        lifecycleScope.launch {
            // Descargar historial desde Oracle
            val historial = OracleRemoteDataSource.obtenerHistorialSalud(idPaciente)

            // SOLUCIÓN AL ERROR DE AMBIGÜEDAD:
            // Filtramos los nulos primero y luego sumamos enteros simples.
            val totalPasos = historial.mapNotNull { it.pasos }.sum()

            // Actualizar UI
            binding.tvPasosActuales.text = "$totalPasos"

            // Cálculo aproximado de calorías (0.04 cal/paso)
            val calorias = (totalPasos * 0.04).toInt()
            binding.tvCalorias.text = "$calorias kcal"
        }
    }
}