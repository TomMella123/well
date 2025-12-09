package com.example.wellfit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.wellfit.databinding.ActivityLoginBinding
import com.example.wellfit.ui.dashboard.DashboardActivity
import com.example.wellfit.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar / ocultar contraseña
        binding.btnVerPassword.setOnClickListener {
            val edit = binding.etPassword
            val visible = edit.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ==
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            edit.inputType = if (visible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            edit.setSelection(edit.text?.length ?: 0)
        }

        // Botón de login
        binding.btnLogin.setOnClickListener {
            val correo = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (correo.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                // Llama al ViewModel que usa OracleRemoteDataSource.loginPaciente(...)
                authViewModel.loginRemoto(correo, pass)
            }
        }

        // Ir a registro  (OJO: en el XML se llama btnGoRegister)
        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, com.example.wellfit.ui.register.RegisterActivity::class.java))
        }

        // Observa resultado de login
        authViewModel.loginPaciente.observe(this) { paciente ->
            if (paciente != null) {
                Toast.makeText(this, "Bienvenido ${paciente.nombre}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Correo o contraseña inválidos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
