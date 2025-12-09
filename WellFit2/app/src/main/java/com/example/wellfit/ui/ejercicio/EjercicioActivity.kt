package com.example.wellfit.ui.ejercicio

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.local.AppDatabase
import com.example.wellfit.data.repository.EjercicioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EjercicioActivity : AppCompatActivity() {

    private lateinit var rvEjercicios: RecyclerView
    private lateinit var tvVacios: TextView
    private lateinit var adapter: EjercicioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejercicio)

        rvEjercicios = findViewById(R.id.rvEjercicios)
        tvVacios = findViewById(R.id.tvEjerciciosVacios)

        adapter = EjercicioAdapter { ejercicio ->
            // Aquí puedes abrir una pantalla de detalle o empezar la rutina
            // Por ahora solo podrías hacer un Toast si quieres.
            // Toast.makeText(this, "Iniciar ${ejercicio.nombreEjercicio}", Toast.LENGTH_SHORT).show()
        }

        rvEjercicios.layoutManager = LinearLayoutManager(this)
        rvEjercicios.adapter = adapter

        cargarEjerciciosDesdeBD()
    }

    private fun cargarEjerciciosDesdeBD() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@EjercicioActivity)
            val repo = EjercicioRepository(db.ejercicioDao())
            val lista = repo.getEjercicios()

            withContext(Dispatchers.Main) {
                if (lista.isEmpty()) {
                    tvVacios.visibility = View.VISIBLE
                    rvEjercicios.visibility = View.GONE
                } else {
                    tvVacios.visibility = View.GONE
                    rvEjercicios.visibility = View.VISIBLE
                    adapter.submitList(lista)
                }
            }
        }
    }
}
