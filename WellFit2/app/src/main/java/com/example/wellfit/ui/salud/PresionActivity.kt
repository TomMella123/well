package com.example.wellfit.ui.salud

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
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
import java.text.SimpleDateFormat
import java.util.*

class PresionActivity : ComponentActivity() {

    private val lecturas = mutableListOf<Triple<String, String, String>>()

    private lateinit var tvUltima: TextView
    private lateinit var containerHistorial: LinearLayout
    private lateinit var etSistolica: EditText
    private lateinit var etDiastolica: EditText
    private lateinit var btnIngresar: Button

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
        setContentView(R.layout.activity_presion)

        userPrefs = UserPrefs(this)
        idPaciente = userPrefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        tvUltima = findViewById(R.id.tvPresionUltima)
        containerHistorial = findViewById(R.id.containerHistorialPresion)
        etSistolica = findViewById(R.id.etSistolica)
        etDiastolica = findViewById(R.id.etDiastolica)
        btnIngresar = findViewById(R.id.btnIngresarPresion)

        btnIngresar.setOnClickListener { registrarNuevaPresion() }

        observarHealthData()
        saludViewModel.cargarHealthData(idPaciente)
    }

    private fun registrarNuevaPresion() {
        val textoSys = etSistolica.text.toString().trim()
        val textoDia = etDiastolica.text.toString().trim()

        if (textoSys.isEmpty() || textoDia.isEmpty()) {
            Toast.makeText(this, "Ingresa ambos valores", Toast.LENGTH_SHORT).show()
            return
        }

        val sys = textoSys.toIntOrNull()
        val dia = textoDia.toIntOrNull()

        if (sys == null || dia == null) {
            Toast.makeText(this, "Valores no válidos", Toast.LENGTH_SHORT).show()
            return
        }

        if (idPaciente == 0L) {
            Toast.makeText(this, "Paciente no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaDb = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("es", "CL")).format(Date())

        val entidad = UserHealthDataEntity(
            fechaData = fechaDb,
            presionSistolica = sys,
            presionDiastolica = dia,
            glucosaSangre = null,
            aguaVasos = null,
            pasos = null,
            idPaciente = idPaciente
        )

        saludViewModel.registrarIndicadores(entidad)

        etSistolica.text.clear()
        etDiastolica.text.clear()
    }

    private fun observarHealthData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                saludViewModel.healthData.collect { lista ->
                    val presiones = lista.filter {
                        it.idPaciente == idPaciente &&
                                it.presionSistolica != null &&
                                it.presionDiastolica != null
                    }

                    lecturas.clear()

                    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("es", "CL"))
                    val sdfFecha = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "CL"))
                    val sdfHora = SimpleDateFormat("HH:mm 'hrs'", Locale("es", "CL"))

                    presiones.forEach { data ->
                        val sys = data.presionSistolica?.toString() ?: return@forEach
                        val dia = data.presionDiastolica?.toString() ?: return@forEach

                        val fechaTexto = try {
                            val date = parser.parse(data.fechaData)
                            if (date != null) {
                                val f = sdfFecha.format(date)
                                val h = sdfHora.format(date)
                                "$f\n$h"
                            } else data.fechaData
                        } catch (e: Exception) {
                            data.fechaData
                        }

                        lecturas.add(Triple(sys, dia, fechaTexto))
                    }

                    refrescar()
                }
            }
        }
    }

    private fun refrescar() {
        if (lecturas.isEmpty()) {
            tvUltima.text = "0/0 mm/Hg"
            containerHistorial.removeAllViews()
            return
        }

        val ultima = lecturas.last()
        tvUltima.text = "${ultima.first}/${ultima.second} mm/Hg"

        containerHistorial.removeAllViews()
        val inflater = LayoutInflater.from(this)

        lecturas.reversed().forEach { (sys, dia, fechaHora) ->
            val item = inflater.inflate(
                R.layout.item_registro_presion,
                containerHistorial,
                false
            )

            item.findViewById<TextView>(R.id.tvValorPresionItem).text =
                "$sys/$dia mm/Hg"
            item.findViewById<TextView>(R.id.tvFechaPresionItem).text =
                fechaHora

            containerHistorial.addView(item)
        }
    }
}
