package com.example.wellfit.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.ui.ejercicio.EjercicioActivity
import com.example.wellfit.ui.hidratacion.HidratacionActivity
import com.example.wellfit.ui.perfil.PerfilActivity
import com.example.wellfit.ui.recetas.RecetasActivity
import com.example.wellfit.ui.salud.GlucosaActivity
import com.example.wellfit.ui.salud.PasosActivity
import com.example.wellfit.ui.salud.PresionActivity
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {
    private var idPaciente: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L
        findViewById<TextView>(R.id.tvHola).text = "Hola, ${prefs.getString("nombre") ?: "Usuario"}"

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (idPaciente != 0L) cargarDatos()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnGlucosa).setOnClickListener { startActivity(Intent(this, GlucosaActivity::class.java)) }
        findViewById<View>(R.id.btnPresion).setOnClickListener { startActivity(Intent(this, PresionActivity::class.java)) }
        findViewById<View>(R.id.btnPasos).setOnClickListener { startActivity(Intent(this, PasosActivity::class.java)) }
        findViewById<View>(R.id.btnHidratacion).setOnClickListener { startActivity(Intent(this, HidratacionActivity::class.java)) }
        findViewById<View>(R.id.btnEjercicio).setOnClickListener { startActivity(Intent(this, EjercicioActivity::class.java)) }
        findViewById<View>(R.id.btnRecetas).setOnClickListener { startActivity(Intent(this, RecetasActivity::class.java)) }
        findViewById<View>(R.id.navPerfil).setOnClickListener { startActivity(Intent(this, PerfilActivity::class.java)) }
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            val historial = OracleRemoteDataSource.obtenerHistorialSalud(idPaciente)
            val desafios = OracleRemoteDataSource.obtenerDesafios()

            // 1. Glucosa
            val gl = historial.filter { it.glucosaSangre != null }.maxByOrNull { it.fechaData + it.idData }
            findViewById<TextView>(R.id.tvGlucosaDashboardValor).text = if (gl != null) "${gl.glucosaSangre} mg/dl" else "00 mg/dl"

            // 2. Presión
            val pr = historial.filter { it.presionSistolica != null }.maxByOrNull { it.fechaData + it.idData }
            findViewById<TextView>(R.id.tvPresionDashboardValor).text = if (pr != null) "${pr.presionSistolica}/${pr.presionDiastolica}" else "00/00"

            // 3. Hidratación
            val ag = historial.filter { it.aguaVasos != null }.maxByOrNull { it.fechaData + it.idData }
            findViewById<TextView>(R.id.tvHidratacionDashboardValor).text = if (ag != null) "${ag.aguaVasos}/8 vasos" else "0/8"

            // 4. Desafíos Count
            findViewById<TextView>(R.id.tvDesafiosCount).text = "${desafios.size} disponibles"
        }
    }
}