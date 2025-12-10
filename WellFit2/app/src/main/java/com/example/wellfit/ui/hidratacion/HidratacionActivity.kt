package com.example.wellfit.ui.hidratacion

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import java.util.Date

// data class AguaItem está definida SÓLO en HidratacionAdapter.kt

class HidratacionActivity : BaseActivity() {

    private val viewModel: SaludViewModel by viewModels()
    private lateinit var adapter: HidratacionAdapter

    // UI Referencias
    private lateinit var ivCiclo: ImageView
    private lateinit var tvContador: TextView
    private lateinit var btnMas: Button
    private lateinit var btnMenos: Button
    private lateinit var rvHistorial: RecyclerView

    // Lógica
    private var vasosActuales = 0
    private val META_DIARIA = 8
    private val LIMITE_PELIGROSO = 14
    private val ML_POR_VASO = 250

    // MEMORIA COMPARTIDA (Para persistencia de 24h y sincronización con Dashboard)
    private val PREFS_NAME = "AguaCache" // Nombre de SharedPreferences
    private val KEY_HISTORY_BASE = "historial_agua_24h" // Clave para el historial de eventos (Lista)
    private val KEY_VASOS_COUNT = "vasos_hoy_count" // Clave para el contador (dashboard)
    private var idPaciente: Long = 0L

    private fun getHistoryKey(): String {
        // Clave de historial única por paciente
        return if (idPaciente == 0L) KEY_HISTORY_BASE else "${KEY_HISTORY_BASE}_$idPaciente"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidratacion)

        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        inicializarVistas()
        setupRecyclerView()
        setupListeners()
        cargarDatosLocales() // Ahora carga el historial, lo filtra y actualiza el contador

        viewModel.historialAgua.observe(this) { items ->
            // Si el ViewModel tuviera datos de la nube, se actualizarían aquí:
            // adapter.actualizarLista(items)
        }

        viewModel.loadHistorialAgua(idPaciente) // Carga datos de la nube
        actualizarUI()
    }

    private fun inicializarVistas() {
        ivCiclo = findViewById(R.id.ivCicloAgua)
        tvContador = findViewById(R.id.txtVasosActuales)
        btnMas = findViewById(R.id.btnMas)
        btnMenos = findViewById(R.id.btnMenos)
        rvHistorial = findViewById(R.id.recyclerHistorialAgua)
    }

    private fun setupRecyclerView() {
        rvHistorial.layoutManager = LinearLayoutManager(this)
        adapter = HidratacionAdapter(mutableListOf())
        rvHistorial.adapter = adapter
    }

    private fun setupListeners() {
        btnMas.setOnClickListener {
            if (idPaciente != 0L) {
                agregarVaso(idPaciente)
            } else {
                Toast.makeText(this, "Error: No hay sesión de paciente", Toast.LENGTH_SHORT).show()
            }
        }

        btnMenos.setOnClickListener {
            quitarVaso()
        }
    }

    private fun agregarVaso(idPaciente: Long) {
        vasosActuales++

        // 1. Guardar en Base de Datos Remota
        viewModel.registrarDatosSalud(idPaciente = idPaciente, agua = ML_POR_VASO)

        // 2. Crear y agregar registro local (persistencia 24h)
        val nuevoRegistro = AguaItem("$ML_POR_VASO ML", System.currentTimeMillis())
        adapter.agregarItem(nuevoRegistro)
        guardarDatoLocal(nuevoRegistro) // Guarda el historial Y actualiza KEY_VASOS_COUNT

        // 3. Actualizar UI
        actualizarUI()
        verificarAlertas()
    }

    private fun quitarVaso() {
        if (vasosActuales > 0) {
            vasosActuales--
            // 1. Quitar de la lista visual y del almacenamiento local
            adapter.eliminarUltimo()
            eliminarUltimoDatoLocal() // Elimina del historial Y actualiza KEY_VASOS_COUNT

            // 2. Actualizar UI
            actualizarUI()
        }
    }

    private fun actualizarUI() {
        tvContador.text = "$vasosActuales/$META_DIARIA"
        val imagenRes = obtenerImagenPorVaso(vasosActuales)
        ivCiclo.setImageResource(imagenRes)

        findViewById<View>(R.id.recyclerHistorialAgua).visibility =
            if (adapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    // --- LÓGICA DE PERSISTENCIA (24 Horas Corregida) ---

    private fun guardarDatoLocal(registro: AguaItem) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialActual = sharedPref.getString(getHistoryKey(), "") ?: ""

        // Formato: "cantidad|tiempo;cantidad|tiempo"
        val nuevoRegistroStr = "${registro.cantidad}|${registro.timestamp}"

        // Agregamos el nuevo registro al principio del string
        val nuevoHistorial = if (historialActual.isEmpty()) {
            nuevoRegistroStr
        } else {
            "$nuevoRegistroStr;$historialActual"
        }

        sharedPref.edit().putString(getHistoryKey(), nuevoHistorial).apply()

        // CRUCIAL PARA EL DASHBOARD: Actualizar el contador persistente
        sharedPref.edit().putInt(KEY_VASOS_COUNT, vasosActuales).apply()
    }

    private fun eliminarUltimoDatoLocal() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialString = sharedPref.getString(getHistoryKey(), "") ?: ""

        if (historialString.isNotEmpty()) {
            val registros = historialString.split(";")
            // Elimina el más reciente (index 0)
            val nuevosRegistros = registros.drop(1).joinToString(";")

            sharedPref.edit().putString(getHistoryKey(), nuevosRegistros).apply()

            // CRUCIAL PARA EL DASHBOARD: Actualizar el contador persistente
            sharedPref.edit().putInt(KEY_VASOS_COUNT, vasosActuales).apply()
        }
    }

    private fun cargarDatosLocales() {
        adapter.actualizarLista(mutableListOf()) // Limpiar lista visual

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historialString = sharedPref.getString(getHistoryKey(), "") ?: ""

        if (historialString.isEmpty()) {
            vasosActuales = 0
            // Si el historial está vacío, resetear el contador del dashboard
            sharedPref.edit().putInt(KEY_VASOS_COUNT, 0).apply()
            actualizarUI()
            return
        }

        val registros = historialString.split(";")
        val registrosValidos = mutableListOf<String>()
        val ahora = System.currentTimeMillis()
        val milisegundosEn24h = 24 * 60 * 60 * 1000

        // Recorremos de atrás hacia adelante para reconstruir la lista visualmente (más reciente arriba)
        for (registro in registros.reversed()) {
            if (registro.isNotEmpty()) {
                val partes = registro.split("|")
                if (partes.size == 2) {
                    val cantidadStr = partes[0]
                    val tiempo = partes[1].toLongOrNull()

                    if (tiempo != null) {
                        // FILTRO DE 24 HORAS
                        if (ahora - tiempo < milisegundosEn24h) {
                            val item = AguaItem(cantidadStr, tiempo)
                            // Agregar al inicio de la lista visual
                            adapter.agregarItem(item)
                            registrosValidos.add(0, registro)
                        }
                    }
                }
            }
        }

        // El nuevo contador es la cantidad de registros válidos
        vasosActuales = registrosValidos.size

        // Actualizamos la memoria del teléfono eliminando los registros viejos (más de 24h)
        sharedPref.edit().putString(getHistoryKey(), registrosValidos.joinToString(";")).apply()

        // CRUCIAL PARA EL DASHBOARD: Actualizar el contador persistente con el valor filtrado
        sharedPref.edit().putInt(KEY_VASOS_COUNT, vasosActuales).apply()

        actualizarUI()
    }
    // --- Fin Lógica de Persistencia ---

    private fun verificarAlertas() {
        if (vasosActuales == META_DIARIA + 1) {
            mostrarAlerta("¡Meta Cumplida!", "Has llegado a tu objetivo diario. Bebe con moderación.", android.R.drawable.ic_dialog_info)
        } else if (vasosActuales > LIMITE_PELIGROSO) {
            mostrarAlerta("¡ALERTA DE SALUD!", "Estás bebiendo demasiada agua. Detente para evitar una intoxicación por agua.", android.R.drawable.ic_dialog_alert)
        }
    }

    private fun mostrarAlerta(titulo: String, msg: String, icon: Int) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(msg)
            .setIcon(icon)
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun obtenerImagenPorVaso(cantidad: Int): Int {
        return when (cantidad) {
            0 -> R.drawable.img_1
            1 -> R.drawable.img_2
            2 -> R.drawable.img_3
            3 -> R.drawable.img_4
            4 -> R.drawable.img_5
            5 -> R.drawable.img_6
            6 -> R.drawable.img_7
            7 -> R.drawable.img_8
            8 -> R.drawable.img_9
            in 9..14 -> R.drawable.img_10
            else -> R.drawable.img_11
        }
    }
}