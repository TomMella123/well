package com.example.wellfit.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

    // Claves de persistencia de Hidratación (deben coincidir con HidratacionActivity.kt)
    private val PREFS_NAME_AGUA = "AguaCache"
    private val KEY_VASOS_COUNT = "vasos_hoy_count"

    // Vistas para los contadores (usando las IDs de activity_dashboard.xml)
    private lateinit var tvDesafiosCount: TextView
    private lateinit var tvEjercicioResumen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        val nombreUsuario = prefs.getString("nombre") ?: "Usuario"
        findViewById<TextView>(R.id.tvHola).text = "Hola, $nombreUsuario"

        inicializarVistas()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        // Cargamos datos remotos para salud general
        cargarDatosRemotos()
        // Cargamos datos locales para tener el agua sincronizada al instante
        cargarDatosAguaLocal() // LLAMADA CORREGIDA

        // Cargar los nuevos contadores
        cargarContadores()
    }

    private fun inicializarVistas() {
        // Inicializar los TextViews de contadores de Desafíos y Ejercicios
        tvDesafiosCount = findViewById(R.id.tvDesafiosCount)
        tvEjercicioResumen = findViewById(R.id.tvEjercicioResumen)
    }


    private fun setupButtons() {
        findViewById<View>(R.id.btnGlucosa).setOnClickListener { startActivity(Intent(this, GlucosaActivity::class.java)) }
        findViewById<View>(R.id.btnPresion).setOnClickListener { startActivity(Intent(this, PresionActivity::class.java)) }
        findViewById<View>(R.id.btnHidratacion).setOnClickListener { startActivity(Intent(this, HidratacionActivity::class.java)) }
        findViewById<View>(R.id.btnPasos).setOnClickListener { startActivity(Intent(this, PasosActivity::class.java)) }

        findViewById<View>(R.id.btnDesafios).setOnClickListener { startActivity(Intent(this, com.example.wellfit.ui.desafios.DesafiosActivity::class.java)) }
        findViewById<View>(R.id.btnEjercicio).setOnClickListener { startActivity(Intent(this, EjercicioActivity::class.java)) }
        findViewById<View>(R.id.btnRecetas).setOnClickListener { startActivity(Intent(this, RecetasActivity::class.java)) }
        findViewById<View>(R.id.navPerfil).setOnClickListener { startActivity(Intent(this, PerfilActivity::class.java)) }
    }

    // Lee la memoria compartida con HidratacionActivity para mostrar "X/8"
    private fun cargarDatosAguaLocal() {
        if (idPaciente == 0L) {
            // Si el ID es 0, no hay usuario válido, mostramos 0/8
            findViewById<TextView>(R.id.tvHidratacionDashboardValor).text = "0/8"
            return
        }

        // CORRECCIÓN: Usar el nombre de archivo y clave que HidratacionActivity utiliza ahora.
        val prefs = getSharedPreferences(PREFS_NAME_AGUA, Context.MODE_PRIVATE)

        // El HidratacionActivity ya maneja el filtro de 24h y actualiza esta clave.
        val vasos = prefs.getInt(KEY_VASOS_COUNT, 0)

        // Buscamos el TextView de hidratación en el dashboard y ponemos el formato "X/8"
        findViewById<TextView>(R.id.tvHidratacionDashboardValor).text = "$vasos/8"
    }

    private fun cargarDatosRemotos() {
        lifecycleScope.launch {
            if (idPaciente == 0L) return@launch

            val historial = OracleRemoteDataSource.obtenerHistorialSalud(idPaciente)

            // GLUCOSA
            val gl = historial
                .filter { (it.glucosaSangre ?: 0) > 0 }
                .maxByOrNull { it.idData }
            findViewById<TextView>(R.id.tvGlucosaDashboardValor).text =
                if (gl != null) "${gl.glucosaSangre} mg/dl" else "-- mg/dl"

            // PRESIÓN
            val pr = historial
                .filter { (it.presionSistolica ?: 0) > 0 }
                .maxByOrNull { it.idData }
            findViewById<TextView>(R.id.tvPresionDashboardValor).text =
                if (pr != null) "${pr.presionSistolica}/${pr.presionDiastolica}" else "--/--"

            // PASOS
            val pasos = historial
                .filter { (it.pasos ?: 0) > 0 }
                .maxByOrNull { it.idData }
            findViewById<TextView>(R.id.tvPasosActuales).text =
                if (pasos != null) "${pasos.pasos}" else "--"

            // NOTA: El agua ya se cargó en cargarDatosAguaLocal(), así que no la sobreescribimos aquí
        }
    }

    private fun cargarContadores() {
        lifecycleScope.launch {
            try {
                // 1. Contar Desafíos
                val desafios = OracleRemoteDataSource.obtenerDesafios()
                val countDesafios = desafios.size

                // Actualizar Desafíos: "X actuales"
                tvDesafiosCount.text = if (countDesafios > 0) {
                    "$countDesafios actuales"
                } else {
                    "0 actuales"
                }

                // 2. Contar Ejercicios
                val ejercicios = OracleRemoteDataSource.obtenerEjercicios()
                val countEjercicios = ejercicios.size

                // Actualizar Ejercicios: "X sugerencias"
                tvEjercicioResumen.text = if (countEjercicios > 0) {
                    "$countEjercicios sugerencias"
                } else {
                    "0 sugerencias"
                }

            } catch (e: Exception) {
                Log.e("Dashboard", "Error cargando contadores: ${e.message}")
                Toast.makeText(this@DashboardActivity, "Error al cargar contadores de desafíos/ejercicios.", Toast.LENGTH_SHORT).show()

                tvDesafiosCount.text = "Error"
                tvEjercicioResumen.text = "Error"
            }
        }
    }
}