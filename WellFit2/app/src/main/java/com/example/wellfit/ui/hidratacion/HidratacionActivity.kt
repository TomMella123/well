package com.example.wellfit.ui.hidratacion

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.local.entities.AguaRegistroEntity
import com.example.wellfit.databinding.ActivityHidratacionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HidratacionActivity : BaseActivity() {

    private lateinit var binding: ActivityHidratacionBinding
    private lateinit var adapter: HidratacionAdapter

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val dao by lazy { db.saludDao() }

    private lateinit var prefs: UserPrefs
    private var idPaciente: Long = 0L

    private val ML_POR_VASO = 250
    private val META_VASOS = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHidratacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        configurarRecycler()
        cargarHistorial()

        binding.btnMas.setOnClickListener { agregarVaso() }
        binding.btnMenos.setOnClickListener { eliminarUltimoRegistro() }
    }

    private fun configurarRecycler() {
        adapter = HidratacionAdapter(emptyList())
        binding.recyclerHistorialAgua.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistorialAgua.adapter = adapter
    }

    private fun cargarHistorial() {
        CoroutineScope(Dispatchers.IO).launch {
            val historial = dao.getHistorialAgua(idPaciente)

            val totalMl = historial.sumOf { it.ml }
            val vasos = totalMl / ML_POR_VASO

            withContext(Dispatchers.Main) {
                adapter.updateData(historial)
                actualizarProgreso(vasos)
            }
        }
    }

    private fun agregarVaso() {
        CoroutineScope(Dispatchers.IO).launch {
            val registro = AguaRegistroEntity(
                idPaciente = idPaciente,
                ml = ML_POR_VASO,
                timestamp = System.currentTimeMillis()
            )
            dao.insertAguaRegistro(registro)
            cargarHistorial()
        }
    }

    private fun eliminarUltimoRegistro() {
        CoroutineScope(Dispatchers.IO).launch {
            val historial = dao.getHistorialAgua(idPaciente)
            if (historial.isNotEmpty()) {
                val ultimo = historial.last()
                dao.eliminarRegistroAgua(ultimo.id)
                cargarHistorial()
            }
        }
    }

    private fun actualizarProgreso(vasos: Int) {
        binding.txtVasosActuales.text = "$vasos/$META_VASOS"

        val percentage = (vasos.toFloat() / META_VASOS.toFloat()).coerceIn(0f, 1f)
        binding.circleContainer.scaleX = 0.8f + (percentage * 0.2f)
        binding.circleContainer.scaleY = 0.8f + (percentage * 0.2f)
    }
}
