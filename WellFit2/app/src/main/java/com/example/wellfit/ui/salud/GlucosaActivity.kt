package com.example.wellfit.ui.salud

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.viewmodel.SaludViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Si GlucosaAdapter.kt no se carga automáticamente, asegúrate de que esté disponible.
// Asumo que tienes una clase GlucosaAdapter definida en otro archivo.

// BORRADO: data class RegistroGlucosa(val valor: Int, val fecha: String)
// **Se resuelve el error de Redeclaration: data class RegistroGlucosa**

class GlucosaActivity : BaseActivity() {
    private val viewModel: SaludViewModel by viewModels()

    // Referencias UI
    private lateinit var tvValorHeader: TextView
    private lateinit var tvEstadoHeader: TextView
    private lateinit var layoutHeader: View
    private lateinit var containerHistorial: LinearLayout
    private lateinit var inputGlucosa: EditText

    // Clave para guardar datos en el teléfono (Atada al ID del paciente)
    private val PREFS_NAME = "GlucosaCache"
    private val KEY_HISTORY_BASE = "historial_24h"
    private var idPaciente: Long = 0L // Se define aquí

    // Helper para obtener la clave de historial por usuario
    private fun getHistoryKey(): String {
        return if (idPaciente == 0L) KEY_HISTORY_BASE else "${KEY_HISTORY_BASE}_$idPaciente"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucosa)

        // Cargar ID de Paciente
        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        inicializarVistas()

        // 1. Cargar datos guardados (si existen y tienen menos de 24h)
        cargarHistorialLocal()

        findViewById<Button>(R.id.btnIngresarGlucosa).setOnClickListener {
            ocultarTeclado()
            procesarIngreso(idPaciente)
        }
    }

    private fun inicializarVistas() {
        tvValorHeader = findViewById(R.id.tvGlucosaUltima)
        tvEstadoHeader = findViewById(R.id.tvGlucosaEstado)
        containerHistorial = findViewById(R.id.containerHistorialGlucosa)
        inputGlucosa = findViewById(R.id.etNuevaGlucosa)

        val icon = findViewById<ImageView>(R.id.ivGlucosaIcon)
        layoutHeader = icon.parent as View
    }

    private fun procesarIngreso(idPaciente: Long) {
        val valorTexto = inputGlucosa.text.toString()
        val valor = valorTexto.toIntOrNull()

        if (valor != null && idPaciente != 0L) {
            // Guardar en BD remota/local (ViewModel)
            viewModel.registrarDatosSalud(idPaciente = idPaciente, glucosa = valor)

            // Guardar en caché local para mostrarlo si el usuario vuelve pronto
            guardarDatoLocal(valor)

            // Actualizar UI actual (Referencias corregidas)
            actualizarEncabezado(valor)
            agregarItemALista(valor, System.currentTimeMillis())
            mostrarAlerta(valor)

            inputGlucosa.text.clear()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Valor inválido")
                .setMessage("Por favor ingresa un número válido.")
                .setPositiveButton("Ok", null)
                .show()
        }
    }

    private fun actualizarEncabezado(valor: Int) { // Referencia corregida
        tvValorHeader.text = "$valor mg/dl"

        val (colorHex, estadoTexto) = when {
            valor < 70 -> Pair("#0000ff", "Hipoglucemia")
            valor in 70..140 -> Pair("#43A047", "Nivel Normal")
            valor in 141..199 -> Pair("#FB8C00", "Nivel Alto")
            else -> Pair("#B71C1C", "PELIGROSO")
        }

        tvEstadoHeader.text = estadoTexto

        try {
            val background = layoutHeader.background.mutate()
            background.setColorFilter(Color.parseColor(colorHex), PorterDuff.Mode.SRC_IN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mostrarAlerta(valor: Int) {
        val (titulo, mensaje, icono) = when {
            valor < 70 -> Triple("¡Glucosa Baja!", "Nivel bajo ($valor). Consume carbohidratos rápidos.", android.R.drawable.ic_dialog_alert)
            valor in 70..140 -> Triple("Todo en orden", "Nivel saludable ($valor).", android.R.drawable.ic_dialog_info)
            valor in 141..199 -> Triple("Nivel Alto", "Tu glucosa ($valor) está elevada.", android.R.drawable.ic_dialog_alert)
            else -> Triple("¡ALERTA!", "Nivel peligroso ($valor). Busca ayuda médica.", android.R.drawable.ic_dialog_alert)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setIcon(icono)
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun agregarItemALista(valor: Int, timestamp: Long) {
        // Referencia corregida (usando LayoutInflater.from(this) en lugar de crearItemHistorial)
        val view = LayoutInflater.from(this).inflate(R.layout.item_glucosa_card, containerHistorial, false)

        val tvValor = view.findViewById<TextView>(R.id.tvValorItem)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaItem)

        tvValor.text = "$valor mg/dl"

        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        tvFecha.text = format.format(date)

        containerHistorial.addView(view, 0)
    }

    // --- LÓGICA DE PERSISTENCIA (24 Horas) ---

    private fun guardarDatoLocal(valor: Int) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialActual = sharedPref.getString(getHistoryKey(), "") ?: "" // Usa clave dinámica

        // Formato: "valor|tiempo;valor|tiempo"
        val nuevoRegistro = "$valor|${System.currentTimeMillis()}"

        // Agregamos al principio
        val nuevoHistorial = if (historialActual.isEmpty()) {
            nuevoRegistro
        } else {
            "$nuevoRegistro;$historialActual"
        }

        sharedPref.edit().putString(getHistoryKey(), nuevoHistorial).apply() // Usa clave dinámica
    }

    private fun cargarHistorialLocal() { // Nombre de función corregido
        containerHistorial.removeAllViews() // Limpiar antes de cargar

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialString = sharedPref.getString(getHistoryKey(), "") ?: "" // Usa clave dinámica

        if (historialString.isEmpty()) {
            // Si no hay historial, aseguramos que el encabezado esté en 0/default
            actualizarEncabezado(0)
            return
        }

        val registros = historialString.split(";")
        val registrosValidos = StringBuilder()
        val ahora = System.currentTimeMillis()
        val milisegundosEn24h = 24 * 60 * 60 * 1000

        // La cadena está guardada como: "NUEVO;VIEJO;MAS_VIEJO"
        // Recorremos de atrás hacia adelante para reconstruir la lista visualmente (más reciente arriba)
        for (registro in registros.reversed()) {
            if (registro.isNotEmpty()) {
                val partes = registro.split("|")
                if (partes.size == 2) {
                    val valor = partes[0].toIntOrNull()
                    val tiempo = partes[1].toLongOrNull()

                    if (valor != null && tiempo != null) {
                        if (ahora - tiempo < milisegundosEn24h) {
                            agregarItemALista(valor, tiempo)

                            // Reconstruimos el string para guardar solo los válidos
                            if (registrosValidos.isNotEmpty()) {
                                registrosValidos.insert(0, ";")
                            }
                            registrosValidos.insert(0, registro)
                        }
                    }
                }
            }
        }

        // Si la lista original no estaba vacía, el primer registro (index 0) de la cadena original es el más nuevo
        if (registros.isNotEmpty()) {
            val primerRegistro = registros[0].split("|")
            if (primerRegistro.size == 2) {
                val v = primerRegistro[0].toIntOrNull()
                val t = primerRegistro[1].toLongOrNull()
                if (v != null && t != null && (ahora - t < milisegundosEn24h)) {
                    actualizarEncabezado(v)
                }
            }
        } else {
            // Si el historial está vacío (tras el filtro de 24h), reiniciamos el encabezado
            actualizarEncabezado(0)
        }

        // Actualizamos la memoria del teléfono eliminando los viejos
        sharedPref.edit().putString(getHistoryKey(), registrosValidos.toString()).apply()
    }

    private fun ocultarTeclado() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}