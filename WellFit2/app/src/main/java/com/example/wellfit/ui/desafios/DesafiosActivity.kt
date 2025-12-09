package com.example.wellfit.ui.desafios

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.DesafioRemoto
import com.example.wellfit.data.remote.OracleRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DesafiosActivity : AppCompatActivity() {

    private lateinit var rvDesafios: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var tvEmpty: TextView

    private val adapter = DesafioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desafios)

        rvDesafios = findViewById(R.id.rvDesafios)
        progress = findViewById(R.id.progressDesafios)
        tvEmpty = findViewById(R.id.tvDesafiosEmpty)

        rvDesafios.layoutManager = LinearLayoutManager(this)
        rvDesafios.adapter = adapter

        cargarDesafios()
    }

    private fun cargarDesafios() {
        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        rvDesafios.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val lista: List<DesafioRemoto> = OracleRemoteDataSource.obtenerDesafios()

            withContext(Dispatchers.Main) {
                progress.visibility = View.GONE

                if (lista.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    rvDesafios.visibility = View.GONE
                } else {
                    adapter.submitList(lista)
                    rvDesafios.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                }
            }
        }
    }
}
