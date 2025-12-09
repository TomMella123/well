package com.example.wellfit.ui.salud

import android.os.Bundle
import android.widget.Toast
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.local.UserKeys
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.local.entities.UserHealthDataEntity
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.databinding.ActivityGlucosaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GlucosaActivity : BaseActivity() {

    private lateinit var binding: ActivityGlucosaBinding
    private lateinit var prefs: UserPrefs

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val saludDao by lazy { db.saludDao() }

    private var idPaciente: Long = 0L
    private var rutPaciente: Long = 0L
    private var dvPaciente: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlucosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = UserPrefs(this)

        // Cargar datos del usuario logueado (idPaciente, rut y dv)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L
        rutPaciente = prefs.getString(UserKeys.RUT)?.toLongOrNull() ?: 0L
        dvPaciente = prefs.getString(UserKeys.DV) ?: ""

        // IDs según activity_glucosa.xml: btnIngresarGlucosa, etNuevaGlucosa, tvGlucosaUltima :contentReference[oaicite:3]{index=3}
        binding.btnIngresarGlucosa.setOnClickListener {
            guardarLecturaGlucosa()
        }

        // Cargar última lectura local para mostrarla
        cargarUltimaLectura()
    }

    private fun cargarUltimaLectura() {
        CoroutineScope(Dispatchers.IO).launch {
            // DAO: getUltimoRegistroGlucosa() NO recibe parámetros :contentReference[oaicite:4]{index=4}
            val ultimo = saludDao.getUltimoRegistroGlucosa()
            withContext(Dispatchers.Main) {
                if (ultimo != null && ultimo.glucosaSangre != null) {
                    binding.tvGlucosaUltima.text =
                        "${ultimo.glucosaSangre} mg/dl"
                } else {
                    binding.tvGlucosaUltima.text = "00 mg/dl"
                }
            }
        }
    }

    private fun guardarLecturaGlucosa() {
        val texto = binding.etNuevaGlucosa.text.toString().trim()
        if (texto.isEmpty()) {
            Toast.makeText(this, "Ingresa un valor de glucosa", Toast.LENGTH_SHORT).show()
            return
        }

        val valor = texto.toIntOrNull()
        if (valor == null) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (idPaciente == 0L) {
            Toast.makeText(this, "ID de paciente no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        if (rutPaciente == 0L || dvPaciente.isBlank()) {
            Toast.makeText(this, "Rut o DV del paciente no encontrados", Toast.LENGTH_SHORT).show()
            return
        }

        // Fecha legible para guardar en Room
        val sdfLocal = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val fechaLocal = sdfLocal.format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            // 1) Guardar en Room para Dashboard y gráficas
            val entidad = UserHealthDataEntity(
                fechaData = fechaLocal,
                presionSistolica = null,
                presionDiastolica = null,
                glucosaSangre = valor,
                aguaVasos = null,
                pasos = null,
                idPaciente = idPaciente,
                pendingSync = false   // ya sincronizamos remoto en este flujo
            )
            saludDao.insertUserHealthData(entidad)

            // 2) Enviar remoto a ORDS /user_health_data usando RUT + DV
            val okRemoto = try {
                OracleRemoteDataSource.crearDatoSaludRemoto(
                    rut = rutPaciente,
                    dv = dvPaciente,
                    glucosaSangre = valor
                )
            } catch (e: Exception) {
                false
            }

            withContext(Dispatchers.Main) {
                if (okRemoto) {
                    Toast.makeText(
                        this@GlucosaActivity,
                        "Glucosa guardada (local + remoto)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@GlucosaActivity,
                        "Glucosa guardada localmente, fallo al sincronizar remoto",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.etNuevaGlucosa.text?.clear()
                cargarUltimaLectura()
            }
        }
    }
}
