package com.example.wellfit.ui.ejercicio

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.databinding.ActivityEjercicioBinding
import com.example.wellfit.viewmodel.EjercicioViewModel

class EjercicioActivity : BaseActivity() {

    private lateinit var binding: ActivityEjercicioBinding
    private val viewModel: EjercicioViewModel by viewModels()
    private val adapter = EjercicioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvEjercicios.layoutManager = LinearLayoutManager(this)
        binding.rvEjercicios.adapter = adapter

        // No hay botón "back" en el header morado de este XML, así que no seteamos listener.

        viewModel.ejercicios.observe(this) { list ->
            if (list.isNotEmpty()) {
                adapter.updateList(list)
                binding.tvEjerciciosVacios.visibility = View.GONE
                binding.rvEjercicios.visibility = View.VISIBLE
            } else {
                binding.rvEjercicios.visibility = View.GONE
                binding.tvEjerciciosVacios.visibility = View.VISIBLE
            }
        }
    }
}