package com.example.wellfit.ui.desafios

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.databinding.ActivityDesafiosBinding
import com.example.wellfit.viewmodel.DesafiosViewModel

class DesafiosActivity : BaseActivity() {

    private lateinit var binding: ActivityDesafiosBinding
    private val viewModel: DesafiosViewModel by viewModels()
    private lateinit var adapter: DesafioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDesafiosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = DesafioAdapter()
        binding.rvDesafios.layoutManager = LinearLayoutManager(this)
        binding.rvDesafios.adapter = adapter
    }

    private fun setupObservers() {
        // Mostrar cargando al inicio
        binding.progressDesafios.visibility = View.VISIBLE

        viewModel.listaDesafios.observe(this) { lista ->
            binding.progressDesafios.visibility = View.GONE

            if (lista.isNotEmpty()) {
                adapter.actualizarLista(lista)
                binding.tvDesafiosEmpty.visibility = View.GONE
                binding.rvDesafios.visibility = View.VISIBLE
            } else {
                binding.rvDesafios.visibility = View.GONE
                binding.tvDesafiosEmpty.visibility = View.VISIBLE
            }
        }
    }
}