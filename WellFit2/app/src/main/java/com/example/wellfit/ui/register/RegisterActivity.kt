package com.example.wellfit.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wellfit.R
import com.example.wellfit.data.remote.EnfermedadRemota
import com.example.wellfit.data.remote.PacientePostRequest
import com.example.wellfit.databinding.ActivityRegisterBinding
import com.example.wellfit.viewmodel.AuthViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    private var enfermedadesDisponibles: List<EnfermedadRemota> = emptyList()
    private val indicesEnfermedadesSeleccionadas = mutableSetOf<Int>()

    // Variables para almacenar datos temporales
    private var ultimoRutRegistro: Long? = null
    private var ultimoDvRegistro: String? = null
    private var ultimasEnfermedadesIds: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar máscara de fecha (dd-MM-yyyy)
        configurarMascaraFecha()

        // 1. Cargar catálogo de enfermedades al iniciar
        authViewModel.cargarEnfermedadesYObjetivos()

        // 2. Observar la lista de enfermedades para el diálogo
        authViewModel.enfermedades.observe(this) { lista ->
            enfermedadesDisponibles = lista
            binding.tvEnfermedadesSeleccionadas.text =
                if (lista.isEmpty()) "Cargando enfermedades..."
                else "Ninguna seleccionada"
        }

        // 3. Botón selección múltiple de enfermedades
        binding.btnSeleccionarEnfermedades.setOnClickListener {
            if (enfermedadesDisponibles.isEmpty()) {
                Toast.makeText(this, "No hay enfermedades cargadas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nombres = enfermedadesDisponibles.map { it.nombreEnfermedad ?: "Sin nombre" }.toTypedArray()
            val checked = BooleanArray(nombres.size) { i -> indicesEnfermedadesSeleccionadas.contains(i) }

            AlertDialog.Builder(this)
                .setTitle("Selecciona tus condiciones")
                .setMultiChoiceItems(nombres, checked) { _, which, isChecked ->
                    if (isChecked) indicesEnfermedadesSeleccionadas.add(which)
                    else indicesEnfermedadesSeleccionadas.remove(which)
                }
                .setPositiveButton("OK") { _, _ ->
                    val seleccion = indicesEnfermedadesSeleccionadas.sorted().map { nombres[it] }
                    binding.tvEnfermedadesSeleccionadas.text =
                        if (seleccion.isEmpty()) "Ninguna seleccionada" else seleccion.joinToString(", ")
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Mostrar/Ocultar contraseña
        binding.btnMostrarPass.setOnClickListener {
            val edit = binding.edtPassword
            val visible = edit.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            edit.inputType = if (visible) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            edit.setSelection(edit.text?.length ?: 0)
        }

        // Botón Registrar
        binding.btnRegistrar.setOnClickListener { registrar() }

        // Observar resultado del Registro
        authViewModel.registroOk.observe(this) { ok ->
            if (ok) {
                AlertDialog.Builder(this)
                    .setTitle("¡Cuenta creada!")
                    .setMessage("Tu registro fue exitoso. Ahora puedes iniciar sesión.")
                    .setCancelable(false)
                    .setPositiveButton("Ir al Login") { _, _ -> finish() }
                    .show()
            } else {
                Toast.makeText(this, "Error al registrar. Verifica tu conexión o el RUT.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun configurarMascaraFecha() {
        binding.edtFecha.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                val digits = s.toString().filter { it.isDigit() }
                val sb = StringBuilder()
                for (i in digits.indices) {
                    sb.append(digits[i])
                    if ((i == 1 || i == 3) && i != digits.lastIndex) sb.append('-')
                }
                binding.edtFecha.setText(sb.toString())
                binding.edtFecha.setSelection(sb.length)
                isEditing = false
            }
        })
    }

    private fun normalizarFecha(fecha: String): String? {
        if (fecha.isBlank()) return null
        val formatos = listOf("ddMMyyyy", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd")
        val salida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (patron in formatos) {
            try {
                val date = SimpleDateFormat(patron, Locale.getDefault()).apply { isLenient = false }.parse(fecha)
                if (date != null) return salida.format(date)
            } catch (_: ParseException) { }
        }
        return null
    }

    private fun registrar() {
        val rutStr = binding.edtRut.text.toString().trim()
        val dv = binding.edtDV.text.toString().trim()
        val nombre = binding.edtNombre.text.toString().trim()
        val email = binding.edtCorreo.text.toString().trim()
        val fechaInput = binding.edtFecha.text.toString().trim()
        val alturaStr = binding.edtAltura.text.toString().trim()
        val pesoStr = binding.edtPeso.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        val genero = when (binding.rgSexo.checkedRadioButtonId) {
            R.id.rbHombre -> "Masculino"
            R.id.rbMujer -> "Femenino"
            else -> "Otros"
        }

        if (rutStr.isEmpty() || dv.isEmpty() || nombre.isEmpty() || email.isEmpty() ||
            fechaInput.isEmpty() || alturaStr.isEmpty() || pesoStr.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaNac = normalizarFecha(fechaInput)
        if (fechaNac == null) {
            Toast.makeText(this, "Fecha inválida (usa dd-mm-aaaa)", Toast.LENGTH_SHORT).show()
            return
        }

        val rut = rutStr.toLongOrNull()
        val altura = alturaStr.toIntOrNull()
        val peso = pesoStr.toIntOrNull()

        if (rut == null || altura == null || peso == null) {
            Toast.makeText(this, "RUT, altura o peso inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener IDs de enfermedades seleccionadas
        val enfermedadesIds = indicesEnfermedadesSeleccionadas.mapNotNull {
            enfermedadesDisponibles.getOrNull(it)?.idEnfermedad
        }

        // --- CREACIÓN DEL REQUEST CORRECTO ---
        val request = PacientePostRequest(
            rut = rut,
            dv = dv,
            nombre = nombre,
            email = email,
            fechaNac = fechaNac,
            genero = genero,
            altura = altura,
            peso = peso,
            idMedico = 30L, // ID médico por defecto
            password = password,
            enfermedades = enfermedadesIds, // Lista de IDs
            imageId = null
        )

        Toast.makeText(this, "Registrando...", Toast.LENGTH_SHORT).show()
        authViewModel.registrarPacienteRemoto(request)
    }
}