package com.example.wellfit.ui.recetas

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.remote.DificultadRemota
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.RecetaRemota
import kotlinx.coroutines.launch
import java.util.Locale

class RecetasActivity : BaseActivity() {

    private lateinit var rvFavoritos: RecyclerView
    private lateinit var rvRecomendadas: RecyclerView
    private lateinit var tvNoFavoritos: TextView
    private lateinit var btnFiltroDificultad: Button

    private lateinit var favoritosAdapter: RecetasAdapter
    private lateinit var recomendadasAdapter: RecetasAdapter

    private var userEnfermedadIds: Set<Long> = emptySet()
    private var todasLasRecetas: List<RecetaRemota> = emptyList()
    private var catalogoDificultades: List<DificultadRemota> = emptyList()
    private var listaFavoritosIds: MutableSet<Long> = mutableSetOf()
    private var dificultadSeleccionadaId: Int? = null
    private var idPaciente: Long = 0L

    // Preferencias locales
    private val PREFS_NAME = "RecetasFavoritos"
    private val KEY_FAVORITOS = "favoritos_ids"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas)

        inicializarVistas()

        val prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        setupRecyclerViews()

        cargarFavoritosLocales()
        cargarCatalogosYRecetas()
    }

    private fun inicializarVistas() {
        rvFavoritos = findViewById(R.id.rvFavoritos)
        rvRecomendadas = findViewById(R.id.rvRecomendadas)
        tvNoFavoritos = findViewById(R.id.tvNoFavoritos)
        btnFiltroDificultad = findViewById(R.id.btnFiltroDificultad)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        val isFavoriteChecker: (Long) -> Boolean = { id -> listaFavoritosIds.contains(id) }
        val clickCallback: (RecetaRemota) -> Unit = { receta -> mostrarDetalleReceta(receta) }
        val toggleCallback: (Long) -> Unit = { id -> toggleFavorito(id) }

        favoritosAdapter = RecetasAdapter(emptyList(), clickCallback, toggleCallback, isFavoriteChecker)
        rvFavoritos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvFavoritos.adapter = favoritosAdapter
        // --- CORRECCIÓN ADICIONAL 1: Deshabilitar el Nested Scrolling para listas anidadas
        rvFavoritos.isNestedScrollingEnabled = false

        recomendadasAdapter = RecetasAdapter(emptyList(), clickCallback, toggleCallback, isFavoriteChecker)
        rvRecomendadas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvRecomendadas.adapter = recomendadasAdapter
        // --- CORRECCIÓN ADICIONAL 1: Deshabilitar el Nested Scrolling para listas anidadas
        rvRecomendadas.isNestedScrollingEnabled = false

        btnFiltroDificultad.setOnClickListener { mostrarDialogoFiltroDificultad() }
    }

    // --- CARGA Y FILTRO DE DATOS ---

    private fun cargarCatalogosYRecetas() {
        lifecycleScope.launch {
            catalogoDificultades = OracleRemoteDataSource.obtenerDificultades()
            todasLasRecetas = OracleRemoteDataSource.obtenerRecetas()
            aplicarFiltrosGlobales()
        }
    }

    private fun cargarEnfermedadesUsuario(idPaciente: Long) {
        lifecycleScope.launch {
            val asociaciones = OracleRemoteDataSource.obtenerAsociacionesEnfermedadPaciente(idPaciente)

            userEnfermedadIds = asociaciones
                .filter { it.idEnfermedad != 61L } // Excluir "No poseo enfermedad"
                .map { it.idEnfermedad }.toSet()
        }
    }

    /**
     * Aplica los filtros a las listas.
     */
    private fun aplicarFiltrosGlobales() {
        if (todasLasRecetas.isEmpty()) return

        // Paso 1: Filtro de Enfermedad completamente ignorado. Usamos todas las recetas.
        val recetasFiltradasPorEnfermedad = todasLasRecetas


        // Paso 2: Filtro por Dificultad (para Recomendadas)
        val recetasRecomendadas = recetasFiltradasPorEnfermedad.filter { receta ->
            dificultadSeleccionadaId == null || receta.idDificultad == dificultadSeleccionadaId
        }

        // Paso 3: Filtrar Favoritos
        val recetasFavoritas = recetasFiltradasPorEnfermedad
            .filter { listaFavoritosIds.contains(it.idReceta) }

        // Paso 4: Actualizar UI
        recomendadasAdapter.updateData(recetasRecomendadas)
        favoritosAdapter.updateData(recetasFavoritas)

        // --- CORRECCIÓN ADICIONAL 2: Forzar la solicitud de Layout
        rvRecomendadas.requestLayout()
        rvFavoritos.requestLayout()

        // Mostrar alerta de "No favoritos"
        tvNoFavoritos.visibility = if (recetasFavoritas.isEmpty()) View.VISIBLE else View.GONE
    }

    // --- GESTIÓN LOCAL DE FAVORITOS ---

    private fun cargarFavoritosLocales() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoritosString = prefs.getString(KEY_FAVORITOS, "") ?: ""

        listaFavoritosIds = favoritosString.split(",")
            .mapNotNull { it.toLongOrNull() }
            .toMutableSet()
    }

    private fun guardarFavoritosLocales() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoritosString = listaFavoritosIds.joinToString(",")
        prefs.edit().putString(KEY_FAVORITOS, favoritosString).apply()
    }

    fun toggleFavorito(recetaId: Long) {
        if (listaFavoritosIds.contains(recetaId)) {
            listaFavoritosIds.remove(recetaId)
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
        } else {
            listaFavoritosIds.add(recetaId)
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
        }
        guardarFavoritosLocales()
        aplicarFiltrosGlobales() // Refresca ambas listas
    }

    // --- DIÁLOGO DE FILTRO ---

    private fun mostrarDialogoFiltroDificultad() {
        val nombresDificultades = mutableListOf("Mostrar Todas")
        val idsDificultades = mutableListOf<Int?>(null)

        catalogoDificultades.forEach { dificultad ->
            nombresDificultades.add(dificultad.nombreDificultad ?: "Desconocida")
            idsDificultades.add(dificultad.idDificultad)
        }

        AlertDialog.Builder(this)
            .setTitle("Filtrar por Dificultad")
            .setSingleChoiceItems(nombresDificultades.toTypedArray(), idsDificultades.indexOf(dificultadSeleccionadaId)) { dialog, which ->
                dificultadSeleccionadaId = idsDificultades[which]

                btnFiltroDificultad.text = nombresDificultades[which]

                aplicarFiltrosGlobales()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- FUNCIÓN PARA MOSTRAR DETALLE (al hacer click) ---

    fun mostrarDetalleReceta(receta: RecetaRemota) {
        val nombre = receta.nombreReceta ?: "Receta sin nombre"
        val pasos = receta.pasosReceta?.split('\n')
            ?.mapIndexed { index, s -> "${index + 1}. $s" }
            ?.joinToString("\n") ?: "Pasos no detallados."

        val ingredientesSimulados = listOf(
            "2 rebanadas de pan (Ingrediente 1)",
            "100g de queso (Ingrediente 2)",
            "Mantequilla (Ingrediente 3)"
        ).joinToString("\n• ", prefix = "• ")

        val mensajeDetalle = "Nutrición: 0 Kcal • 0 g de Carb.\n\n" +
                "**Ingredientes:**\n$ingredientesSimulados\n\n" +
                "**Preparación:**\n" +
                pasos

        AlertDialog.Builder(this)
            .setTitle(nombre)
            .setMessage(mensajeDetalle)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}