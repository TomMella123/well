package com.example.wellfit.ui.salud

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.viewmodel.SaludViewModel

class GlucosaActivity : BaseActivity() {
    private val viewModel: SaludViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucosa)

        val prefs = UserPrefs(this)
        val idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L

        findViewById<android.view.View>(R.id.btnIngresarGlucosa).setOnClickListener {
            val input = findViewById<EditText>(R.id.etNuevaGlucosa)
            val valor = input.text.toString().toIntOrNull()

            if (valor != null && idPaciente != 0L) {
                viewModel.registrarDatosSalud(idPaciente = idPaciente, glucosa = valor)
                input.text.clear()
                Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: Revisa el valor o tu sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}