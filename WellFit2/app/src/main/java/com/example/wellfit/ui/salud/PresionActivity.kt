package com.example.wellfit.ui.salud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.databinding.ActivityPresionBinding
import com.example.wellfit.viewmodel.SaludViewModel

class PresionActivity : BaseActivity() {

    private lateinit var binding: ActivityPresionBinding
    private val viewModel: SaludViewModel by viewModels()
    private var idPaciente: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = UserPrefs(this)
        val idString = prefs.getString("idPaciente")
        idPaciente = idString?.toLongOrNull() ?: 0L

        binding.btnIngresarPresion.setOnClickListener {
            val sisStr = binding.etSistolica.text.toString()
            val diasStr = binding.etDiastolica.text.toString()

            if (sisStr.isNotEmpty() && diasStr.isNotEmpty()) {
                val sis = sisStr.toIntOrNull()
                val dias = diasStr.toIntOrNull()

                if (idPaciente != 0L) {
                    viewModel.registrarDatosSalud(idPaciente = idPaciente, presionSis = sis, presionDias = dias)
                } else {
                    Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa ambos valores", Toast.LENGTH_SHORT).show()
            }
        }

        // CORRECCIÓN: Especificamos explícitamente que recibimos un Boolean
        viewModel.operacionExitosa.observe(this) { exito: Boolean ->
            if (exito) {
                Toast.makeText(this, "Guardado en Oracle", Toast.LENGTH_SHORT).show()
                binding.etSistolica.text.clear()
                binding.etDiastolica.text.clear()
            } else {
                Toast.makeText(this, "Error al guardar (Verifica internet)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}