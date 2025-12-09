package com.example.wellfit.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.ui.ejercicio.EjercicioActivity
import com.example.wellfit.ui.hidratacion.HidratacionActivity
import com.example.wellfit.ui.perfil.PerfilActivity
import com.example.wellfit.ui.recetas.RecetasActivity
import com.example.wellfit.ui.salud.GlucosaActivity
import com.example.wellfit.ui.salud.PasosActivity
import com.example.wellfit.ui.salud.PresionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : BaseActivity() {

    private lateinit var prefs: UserPrefs

    // TextViews que SÍ existen en activity_dashboard.xml
    private lateinit var tvHolaNombre: TextView
    private lateinit var tvGlucosaValor: TextView
    private lateinit var tvPresionValor: TextView

    // Cards / botones (IDs reales en el XML)
    private lateinit var btnGlucosa: LinearLayout
    private lateinit var btnPresion: LinearLayout
    private lateinit var btnHidratacion: LinearLayout
    private lateinit var btnPasos: LinearLayout
    private lateinit var btnRecetas: LinearLayout
    private lateinit var btnEjercicio: LinearLayout

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val saludDao by lazy { db.saludDao() }

    private var idPaciente: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        // IDs según activity_dashboard.xml :contentReference[oaicite:0]{index=0}
        tvHolaNombre = findViewById(R.id.tvHola)
        tvGlucosaValor = findViewById(R.id.tvGlucosaDashboardValor)
        tvPresionValor = findViewById(R.id.tvPresionDashboardValor)

        btnGlucosa = findViewById(R.id.btnGlucosa)
        btnPresion = findViewById(R.id.btnPresion)
        btnHidratacion = findViewById(R.id.btnHidratacion)
        btnPasos = findViewById(R.id.btnPasos)
        btnRecetas = findViewById(R.id.btnRecetas)
        btnEjercicio = findViewById(R.id.btnEjercicio)

        val nombre = prefs.getString("nombre") ?: "Usuario"
        tvHolaNombre.text = "Hola, $nombre"

        configurarClicks()
        cargarDatosSalud()
    }

    private fun configurarClicks() {
        btnGlucosa.setOnClickListener {
            startActivity(Intent(this, GlucosaActivity::class.java))
        }
        btnPasos.setOnClickListener {
            startActivity(Intent(this, PasosActivity::class.java))
        }
        btnPresion.setOnClickListener {
            startActivity(Intent(this, PresionActivity::class.java))
        }
        btnHidratacion.setOnClickListener {
            startActivity(Intent(this, HidratacionActivity::class.java))
        }
        btnEjercicio.setOnClickListener {
            startActivity(Intent(this, EjercicioActivity::class.java))
        }
        btnRecetas.setOnClickListener {
            startActivity(Intent(this, RecetasActivity::class.java))
        }

        // El perfil lo abres con el botón del círculo blanco de abajo
        findViewById<android.widget.RelativeLayout>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Por si vuelve desde Glucosa/Presión/etc.
        cargarDatosSalud()
    }

    private fun cargarDatosSalud() {
        if (idPaciente == 0L) {
            tvGlucosaValor.text = "00 mg/dl"
            tvPresionValor.text = "00/00 mmHg"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            // OJO: getUltimoRegistroGlucosa() NO recibe idPaciente :contentReference[oaicite:1]{index=1}
            val ultimoGlucosa = saludDao.getUltimoRegistroGlucosa()
            val ultimaPresion = saludDao.getUltimoRegistroPresion(idPaciente)

            withContext(Dispatchers.Main) {

                // Glucosa
                if (ultimoGlucosa != null && ultimoGlucosa.glucosaSangre != null) {
                    tvGlucosaValor.text = "${ultimoGlucosa.glucosaSangre} mg/dl"
                } else {
                    tvGlucosaValor.text = "00 mg/dl"
                }

                // Presión
                if (ultimaPresion != null &&
                    ultimaPresion.presionSistolica != null &&
                    ultimaPresion.presionDiastolica != null
                ) {
                    tvPresionValor.text =
                        "${ultimaPresion.presionSistolica}/${ultimaPresion.presionDiastolica} mmHg"
                } else {
                    tvPresionValor.text = "00/00 mmHg"
                }
            }
        }
    }
}
