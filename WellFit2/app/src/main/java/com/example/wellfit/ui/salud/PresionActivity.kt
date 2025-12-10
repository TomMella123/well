package com.example.wellfit.ui.salud

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.viewmodel.SaludViewModel

// data class PresionItem está definida SÓLO en PresionAdapter.kt

class PresionActivity : BaseActivity() {

    private val viewModel: SaludViewModel by viewModels()
    private lateinit var adapter: PresionAdapter
    private lateinit var rvHistorial: RecyclerView

    // UI
    private lateinit var tvUltimaPresion: TextView
    private lateinit var etSistolica: EditText
    private lateinit var etDiastolica: EditText
    private lateinit var ivHeart: ImageView

    // Claves para PERSISTENCIA DE HISTORIAL (Nueva Lógica 24h)
    private val PREFS_NAME = "PresionCache"
    private val KEY_HISTORY_BASE = "historial_presion_24h"
    private val KEY_LAST_SIS_DASH = "last_sis_dashboard" // Clave para la última lectura
    private val KEY_LAST_DIA_DASH = "last_dia_dashboard" // Clave para la última lectura
    private var idPaciente: Long = 0L

    private fun getHistoryKey(): String {
        return if (idPaciente == 0L) KEY_HISTORY_BASE else "${KEY_HISTORY_BASE}_$idPaciente"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presion)

        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        inicializarVistas()
        setupRecyclerView()
        setupAnimaciones()
        cargarHistorialLocal() // Carga todo el historial de 24h y la última lectura

        viewModel.historialPresion.observe(this) { items ->
            // La lista de la nube/VM se carga aquí, actualizando la lista visual
            // Nota: Aquí podrías querer actualizar solo si la nube es más reciente
            adapter.actualizarLista(items)
        }

        viewModel.loadHistorialSalud(idPaciente)

        findViewById<Button>(R.id.btnIngresarPresion).setOnClickListener {
            procesarIngreso(idPaciente)
        }
    }

    private fun inicializarVistas() {
        tvUltimaPresion = findViewById(R.id.tvPresionUltima)
        etSistolica = findViewById(R.id.etSistolica)
        etDiastolica = findViewById(R.id.etDiastolica)
        ivHeart = findViewById(R.id.ivHeartAnim)
        rvHistorial = findViewById(R.id.rvHistorialPresion)
    }

    private fun setupAnimaciones() {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            ivHeart,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown.duration = 800
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE
        scaleDown.interpolator = AccelerateDecelerateInterpolator()
        scaleDown.start()
    }

    private fun setupRecyclerView() {
        adapter = PresionAdapter(mutableListOf())
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = adapter
    }


    private fun procesarIngreso(idPaciente: Long) {
        val sisStr = etSistolica.text.toString()
        val diasStr = etDiastolica.text.toString()

        if (sisStr.isNotEmpty() && diasStr.isNotEmpty()) {
            val sis = sisStr.toIntOrNull() ?: 0
            val dias = diasStr.toIntOrNull() ?: 0
            val timestamp = System.currentTimeMillis()

            if (idPaciente != 0L) {
                // 1. Guardar en Base de Datos Remota
                viewModel.registrarDatosSalud(idPaciente = idPaciente, presionSis = sis, presionDias = dias)
            }

            // 2. Crear y agregar registro local (persistencia 24h)
            val nuevoRegistro = PresionItem(sis, dias, timestamp)
            adapter.agregarItem(nuevoRegistro)
            guardarLecturaLocal(nuevoRegistro) // **Asegura la persistencia de la última lectura y el historial**

            // 3. Actualizar UI y alertas
            tvUltimaPresion.text = "$sis/$dias"
            mostrarDiagnostico(sis, dias)

            // 4. Limpiar
            etSistolica.text.clear()
            etDiastolica.text.clear()

        } else {
            Toast.makeText(this, "Por favor ingresa ambos valores", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDiagnostico(sis: Int, dias: Int) {
        val (titulo, mensaje, icono) = when {
            sis < 90 || dias < 60 ->
                Triple("Presión Baja", "Tu presión está baja (Hipotensión). Si sientes mareos, siéntate y bebe agua.", android.R.drawable.ic_dialog_alert)

            sis in 90..120 && dias in 60..80 ->
                Triple("¡Excelente!", "Tu presión arterial es normal y saludable. ¡Sigue así!", android.R.drawable.ic_dialog_info)

            sis in 121..139 || dias in 81..89 ->
                Triple("Atención: Pre-hipertensión", "Tus valores están un poco elevados. Intenta reducir la sal y relajarte.", android.R.drawable.ic_dialog_alert)

            sis >= 140 || dias >= 90 ->
                Triple("¡ALERTA DE HIPERTENSIÓN!", "Tu presión está alta. Si se mantiene así, consulta a un médico pronto.", android.R.drawable.ic_dialog_alert)

            else -> Triple("Registro Guardado", "Lectura registrada correctamente.", android.R.drawable.ic_dialog_info)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setIcon(icono)
            .setPositiveButton("Entendido", null)
            .show()
    }

    // --- PERSISTENCIA DE HISTORIAL (24 Horas) ---

    private fun guardarLecturaLocal(registro: PresionItem) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialActual = sharedPref.getString(getHistoryKey(), "") ?: ""

        // Formato: "sis|dias|tiempo;sis|dias|tiempo"
        val nuevoRegistroStr = "${registro.sistolica}|${registro.diastolica}|${registro.timestamp}"

        // Agregamos el nuevo registro al principio del string
        val nuevoHistorial = if (historialActual.isEmpty()) {
            nuevoRegistroStr
        } else {
            "$nuevoRegistroStr;$historialActual"
        }

        sharedPref.edit().putString(getHistoryKey(), nuevoHistorial).apply()

        // **CRUCIAL**: Guardamos la última lectura por separado para que el Dashboard la pueda leer
        sharedPref.edit()
            .putInt(KEY_LAST_SIS_DASH, registro.sistolica)
            .putInt(KEY_LAST_DIA_DASH, registro.diastolica)
            .apply()
    }

    private fun cargarHistorialLocal() {
        adapter.actualizarLista(mutableListOf()) // Limpiar antes de cargar

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialString = sharedPref.getString(getHistoryKey(), "") ?: ""

        // Si no hay historial, cargamos la última lectura guardada (si existe) o 0/0
        if (historialString.isEmpty()) {
            val lastSis = sharedPref.getInt(KEY_LAST_SIS_DASH, 0)
            val lastDias = sharedPref.getInt(KEY_LAST_DIA_DASH, 0)
            tvUltimaPresion.text = if (lastSis != 0) "$lastSis/$lastDias" else "0/0"
            return
        }

        val registros = historialString.split(";")
        val registrosValidos = mutableListOf<String>()
        val ahora = System.currentTimeMillis()
        val milisegundosEn24h = 24 * 60 * 60 * 1000

        var lecturaMasRecienteSis = 0
        var lecturaMasRecienteDias = 0

        // Recorremos el registro DE ATRÁS HACIA ADELANTE para reconstruir la lista
        for (registro in registros.reversed()) {
            if (registro.isNotEmpty()) {
                val partes = registro.split("|")
                if (partes.size == 3) {
                    val sis = partes[0].toIntOrNull()
                    val dias = partes[1].toIntOrNull()
                    val tiempo = partes[2].toLongOrNull()

                    if (sis != null && dias != null && tiempo != null) {
                        // FILTRO DE 24 HORAS
                        if (ahora - tiempo < milisegundosEn24h) {
                            val item = PresionItem(sis, dias, tiempo)
                            // Agregar al inicio de la lista visual
                            adapter.agregarItem(item)
                            registrosValidos.add(0, registro)

                            // La primera lectura que encontramos que es válida es la más reciente
                            if (lecturaMasRecienteSis == 0) {
                                lecturaMasRecienteSis = sis
                                lecturaMasRecienteDias = dias
                            }
                        }
                    }
                }
            }
        }

        // Actualizamos la memoria del teléfono eliminando los registros viejos (más de 24h)
        sharedPref.edit().putString(getHistoryKey(), registrosValidos.joinToString(";")).apply()

        // Si se encontraron lecturas válidas, actualizamos la UI y el Dashboard (en caso de que solo se haya filtrado)
        if (lecturaMasRecienteSis != 0) {
            tvUltimaPresion.text = "$lecturaMasRecienteSis/$lecturaMasRecienteDias"
            // También actualizamos las claves del dashboard con el valor más reciente
            sharedPref.edit()
                .putInt(KEY_LAST_SIS_DASH, lecturaMasRecienteSis)
                .putInt(KEY_LAST_DIA_DASH, lecturaMasRecienteDias)
                .apply()
        } else {
            tvUltimaPresion.text = "0/0"
            sharedPref.edit()
                .putInt(KEY_LAST_SIS_DASH, 0)
                .putInt(KEY_LAST_DIA_DASH, 0)
                .apply()
        }
    }
}