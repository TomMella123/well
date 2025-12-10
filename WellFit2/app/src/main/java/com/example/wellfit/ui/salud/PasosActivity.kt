package com.example.wellfit.ui.salud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.databinding.ActivityPresionBinding
import com.example.wellfit.viewmodel.SaludViewModel

class PresionActivity : BaseActivity() {

    private lateinit var binding: ActivityPresionBinding
    private val viewModel: SaludViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ID CORREGIDO: btnIngresarPresion
        binding.btnIngresarPresion.setOnClickListener {
            val sisStr = binding.etSistolica.text.toString()
            val diasStr = binding.etDiastolica.text.toString()

            if (sisStr.isNotEmpty() && diasStr.isNotEmpty()) {
                val sis = sisStr.toIntOrNull()
                val dias = diasStr.toIntOrNull()

                // TODO: Recuperar ID del usuario real
                val idPaciente = 1L

                viewModel.registrarDatosSalud(
                    idPaciente = idPaciente,
                    presionSis = sis,
                    presionDias = dias
                )
            } else {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.operacionExitosa.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Presión guardada", Toast.LENGTH_SHORT).show()
                binding.etSistolica.text.clear()
                binding.etDiastolica.text.clear()
            } else {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}