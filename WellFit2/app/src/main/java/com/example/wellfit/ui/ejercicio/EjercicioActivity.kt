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
    private val vm: EjercicioViewModel by viewModels()
    private val adapter = EjercicioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvEjercicios.layoutManager = LinearLayoutManager(this)
        binding.rvEjercicios.adapter = adapter

        vm.ejercicios.observe(this) { list ->
            if (list.isNotEmpty()) {
                adapter.update(list)
                binding.tvEjerciciosVacios.visibility = View.GONE
            } else {
                binding.tvEjerciciosVacios.visibility = View.VISIBLE
            }
        }
    }
}