package com.example.wellfit.ui.salud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.databinding.ActivityGlucosaBinding
import com.example.wellfit.viewmodel.SaludViewModel

class GlucosaActivity : BaseActivity() {

    private lateinit var binding: ActivityGlucosaBinding
    private val viewModel: SaludViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlucosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ID CORREGIDO: btnIngresarGlucosa
        binding.btnIngresarGlucosa.setOnClickListener {
            // ID CORREGIDO: etNuevaGlucosa
            val valorStr = binding.etNuevaGlucosa.text.toString()
            if (valorStr.isNotEmpty()) {
                val valor = valorStr.toIntOrNull()

                // TODO: Recuperar el ID real del usuario desde SharedPreferences
                val idPaciente = 1L

                viewModel.registrarDatosSalud(
                    idPaciente = idPaciente,
                    glucosa = valor
                )
            } else {
                Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.operacionExitosa.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Glucosa guardada", Toast.LENGTH_SHORT).show()
                binding.etNuevaGlucosa.text.clear()
                // Opcional: Actualizar el texto superior tvGlucosaUltima
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}