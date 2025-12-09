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

    // Guardar los datos del último registro para asociar enfermedades después
    private var ultimoRutRegistro: Long? = null
    private var ultimoDvRegistro: String? = null
    private var ultimasEnfermedadesIds: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mascara de fecha: 26092002 -> 26-09-2002
        configurarMascaraFecha()

        // Cargar enfermedades (desde Oracle)
        authViewModel.cargarEnfermedadesYObjetivos()

        authViewModel.enfermedades.observe(this) { lista ->
            enfermedadesDisponibles = lista
            binding.tvEnfermedadesSeleccionadas.text =
                if (lista.isEmpty()) "No se pudieron cargar las enfermedades"
                else "Ninguna seleccionada"
        }

        // Botón para seleccionar enfermedades (multi-choice)
        binding.btnSeleccionarEnfermedades.setOnClickListener {
            if (enfermedadesDisponibles.isEmpty()) {
                Toast.makeText(
                    this,
                    "Aún no hay enfermedades para seleccionar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val nombres = enfermedadesDisponibles
                .map { it.nombreEnfermedad ?: "Sin nombre" }
                .toTypedArray()

            val checked = BooleanArray(nombres.size) { i ->
                indicesEnfermedadesSeleccionadas.contains(i)
            }

            AlertDialog.Builder(this)
                .setTitle("Selecciona enfermedades")
                .setMultiChoiceItems(nombres, checked) { _, which, isChecked ->
                    if (isChecked) {
                        indicesEnfermedadesSeleccionadas.add(which)
                    } else {
                        indicesEnfermedadesSeleccionadas.remove(which)
                    }
                }
                .setPositiveButton("OK") { _, _ ->
                    val seleccion = indicesEnfermedadesSeleccionadas
                        .sorted()
                        .map { nombres[it] }

                    binding.tvEnfermedadesSeleccionadas.text =
                        if (seleccion.isEmpty()) "Ninguna seleccionada"
                        else seleccion.joinToString(", ")
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Mostrar / ocultar password
        binding.btnMostrarPass.setOnClickListener {
            val edit = binding.edtPassword
            val visible = edit.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ==
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            edit.inputType = if (visible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            edit.setSelection(edit.text?.length ?: 0)
        }

        // Botón registrar
        binding.btnRegistrar.setOnClickListener {
            registrar()
        }

        // 1) Resultado del registro remoto (solo crear paciente)
        authViewModel.registroOk.observe(this) { ok ->
            if (ok == true) {
                // Mostrar diálogo: cuenta creada
                AlertDialog.Builder(this)
                    .setTitle("Cuenta creada")
                    .setMessage("Tu cuenta se creó correctamente.")
                    .setCancelable(false)
                    .setPositiveButton("Continuar") { _, _ ->
                        val rut = ultimoRutRegistro
                        val dv = ultimoDvRegistro
                        val enfIds = ultimasEnfermedadesIds

                        if (rut != null && !dv.isNullOrBlank() && enfIds.isNotEmpty()) {
                            // 2) Ahora sí, asociamos enfermedades
                            authViewModel.asociarEnfermedades(
                                rut = rut,
                                dv = dv,
                                enfermedades = enfIds
                            )
                        } else {
                            // Si no hay enfermedades, simplemente cerramos
                            Toast.makeText(
                                this,
                                "Paciente creado sin enfermedades asociadas",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                    .show()

            } else if (ok == false) {
                Toast.makeText(this, "Error al crear paciente", Toast.LENGTH_SHORT).show()
            }
        }

        // 3) Resultado de asociar enfermedades (se dispara al apretar "Continuar")
        authViewModel.enfermedadesOk.observe(this) { ok ->
            if (ok == null) return@observe

            if (ok) {
                Toast.makeText(
                    this,
                    "Enfermedades asociadas correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "No se pudieron asociar las enfermedades",
                    Toast.LENGTH_SHORT
                ).show()
            }
            authViewModel.resetEnfermedadesOk()
            finish()
        }
    }

    /**
     * Mascara simple: el usuario escribe solo dígitos (ej: 26092002)
     * y se muestra como 26-09-2002.
     */
    private fun configurarMascaraFecha() {
        binding.edtFecha.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val digits = s.toString().filter { it.isDigit() }
                val builder = StringBuilder()

                for (i in digits.indices) {
                    builder.append(digits[i])
                    if ((i == 1 || i == 3) && i != digits.lastIndex) {
                        builder.append('-')
                    }
                }

                binding.edtFecha.setText(builder.toString())
                binding.edtFecha.setSelection(builder.length)

                isEditing = false
            }
        })
    }

    /**
     * Convierte lo que escribe el usuario a formato ISO "yyyy-MM-dd"
     */
    private fun normalizarFecha(fecha: String): String? {
        if (fecha.isBlank()) return null

        val formatosEntrada = listOf("ddMMyyyy", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd")
        val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (patron in formatosEntrada) {
            val sdf = SimpleDateFormat(patron, Locale.getDefault())
            sdf.isLenient = false
            try {
                val date = sdf.parse(fecha)
                if (date != null) {
                    return formatoSalida.format(date)
                }
            } catch (_: ParseException) {
                // probar siguiente formato
            }
        }
        return null
    }

    private fun registrar() {
        val rutStr = binding.edtRut.text.toString().trim()
        val dv = binding.edtDV.text.toString().trim()
        val nombre = binding.edtNombre.text.toString().trim()
        val email = binding.edtCorreo.text.toString().trim()
        val fechaNacInput = binding.edtFecha.text.toString().trim()
        val alturaStr = binding.edtAltura.text.toString().trim()
        val pesoStr = binding.edtPeso.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        // Genero en TEXTO
        val genero = when (binding.rgSexo.checkedRadioButtonId) {
            R.id.rbHombre -> "Masculino"
            R.id.rbMujer -> "Femenino"
            R.id.rbOtro -> "Otros"
            else -> ""
        }

        if (
            rutStr.isEmpty() || dv.isEmpty() || nombre.isEmpty() ||
            email.isEmpty() || fechaNacInput.isEmpty() || genero.isEmpty() ||
            alturaStr.isEmpty() || pesoStr.isEmpty() || password.isEmpty()
        ) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaNac = normalizarFecha(fechaNacInput)
        if (fechaNac == null) {
            Toast.makeText(
                this,
                "Formato de fecha inválido. Usa 26092002 o 26-09-2002",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val rut = rutStr.toLongOrNull()
        val altura = alturaStr.toIntOrNull()
        val peso = pesoStr.toIntOrNull()

        if (rut == null || altura == null || peso == null) {
            Toast.makeText(this, "Rut/Altura/Peso inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        // IDs REALES de las enfermedades seleccionadas
        val enfermedadesIds: List<Long> =
            indicesEnfermedadesSeleccionadas
                .sorted()
                .mapNotNull { index ->
                    enfermedadesDisponibles.getOrNull(index)?.idEnfermedad
                }

        // Guardamos para usarlos cuando el registro remoto sea OK
        ultimoRutRegistro = rut
        ultimoDvRegistro = dv
        ultimasEnfermedadesIds = enfermedadesIds

        // Por ahora un idMedico fijo
        val idMedico = 30L

        val request = PacientePostRequest(
            rut = rut,
            dv = dv,
            nombre = nombre,
            email = email,
            fechaNac = fechaNac,          // "yyyy-MM-dd"
            genero = genero,              // "Masculino/Femenino/Otros"
            altura = altura,
            peso = peso,
            idMedico = idMedico,
            password = password,
            imageId = null,
            enfermedades = null           // ya no se usan en el POST de paciente
        )

        authViewModel.registrarPacienteRemoto(request)
    }
}
