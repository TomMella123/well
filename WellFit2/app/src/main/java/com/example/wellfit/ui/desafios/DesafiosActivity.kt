package com.example.wellfit.ui.desafios

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.databinding.ActivityDesafiosBinding
import com.example.wellfit.viewmodel.DesafiosViewModel

class DesafiosActivity : BaseActivity() {
    private lateinit var binding: ActivityDesafiosBinding
    private val vm: DesafiosViewModel by viewModels()
    private val adapter = DesafioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDesafiosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvDesafios.layoutManager = LinearLayoutManager(this)
        binding.rvDesafios.adapter = adapter
        binding.progressDesafios.visibility = View.VISIBLE

        vm.listaDesafios.observe(this) { list ->
            binding.progressDesafios.visibility = View.GONE
            if (list.isNotEmpty()) {
                adapter.update(list)
                binding.tvDesafiosEmpty.visibility = View.GONE
            } else {
                binding.tvDesafiosEmpty.visibility = View.VISIBLE
            }
        }
    }
}