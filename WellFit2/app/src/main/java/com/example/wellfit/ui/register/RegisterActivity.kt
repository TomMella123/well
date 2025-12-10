package com.example.wellfit.ui.register

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.remote.EnfermedadRemota
import com.example.wellfit.data.remote.ObjetivoRemoto
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacientePostRequest
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

class RegisterActivity : BaseActivity() {

    // Vistas de TextInputLayout y EditText
    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRut: EditText
    private lateinit var etDv: EditText
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText
    private lateinit var etFechaNac: EditText
    private lateinit var etPassword: EditText

    private lateinit var inputLayoutNombre: TextInputLayout
    private lateinit var inputLayoutEmail: TextInputLayout
    private lateinit var inputLayoutRut: TextInputLayout
    private lateinit var inputLayoutDv: TextInputLayout
    private lateinit var inputLayoutAltura: TextInputLayout
    private lateinit var inputLayoutPeso: TextInputLayout
    private lateinit var inputLayoutFechaNac: TextInputLayout
    private lateinit var inputLayoutPassword: TextInputLayout

    private lateinit var rgSexo: RadioGroup
    private lateinit var spinnerEnfermedades: Spinner
    private lateinit var btnCrearCuenta: Button

    // Contenedor de objetivos
    private lateinit var rgObjetivos: RadioGroup

    // Datos remotos
    private var enfermedadesRemotas: List<EnfermedadRemota> = emptyList()
    private var objetivosRemotos: List<ObjetivoRemoto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inicializarVistas()
        setupListeners()
        cargarEnfermedades()
        cargarObjetivos()
    }

    private fun inicializarVistas() {
        // Enlaces de TextInputLayout
        inputLayoutNombre = findViewById(R.id.inputLayoutNombre)
        inputLayoutEmail = findViewById(R.id.inputLayoutEmail)
        inputLayoutRut = findViewById(R.id.inputLayoutRut)
        inputLayoutDv = findViewById(R.id.inputLayoutDv)
        inputLayoutAltura = findViewById(R.id.inputLayoutAltura)
        inputLayoutPeso = findViewById(R.id.inputLayoutPeso)
        inputLayoutFechaNac = findViewById(R.id.inputLayoutFechaNac)
        inputLayoutPassword = findViewById(R.id.inputLayoutPassword)

        // Enlaces de EditText
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etRut = findViewById(R.id.etRut)
        etDv = findViewById(R.id.etDv)
        etAltura = findViewById(R.id.etAltura)
        etPeso = findViewById(R.id.etPeso)
        etFechaNac = findViewById(R.id.etFechaNac)
        etPassword = findViewById(R.id.etPassword)

        rgSexo = findViewById(R.id.rgSexo)
        spinnerEnfermedades = findViewById(R.id.spinnerEnfermedades)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)

        rgObjetivos = findViewById(R.id.rgObjetivos)

        // Asignación de clics para la imagen
        findViewById<ImageView>(R.id.btnAddImage).setOnClickListener {
            Toast.makeText(this, "Abrir selector de imagen...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        btnCrearCuenta.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }

        etFechaNac.setOnClickListener { mostrarDatePicker() }

        // Validación de RUT en tiempo real
        etRut.addTextChangedListener(rutTextWatcher)
        etDv.addTextChangedListener(rutTextWatcher)

        // Validación de Contraseña en tiempo real
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    inputLayoutPassword.error = "La contraseña debe tener al menos 8 caracteres."
                } else {
                    inputLayoutPassword.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Watcher para validar RUT y DV conjuntamente
    private val rutTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val rut = etRut.text.toString()
            val dv = etDv.text.toString().uppercase()

            if (rut.length == 8 && dv.isNotEmpty()) {
                if (!validarRut(rut.toLong(), dv)) {
                    inputLayoutRut.error = "RUT inválido"
                    inputLayoutDv.error = "DV inválido"
                } else {
                    inputLayoutRut.error = null
                    inputLayoutDv.error = null
                }
            } else {
                if (inputLayoutRut.error != null) inputLayoutRut.error = null
                if (inputLayoutDv.error != null) inputLayoutDv.error = null
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }


    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etFechaNac.setText(dateFormat.format(selectedDate.time))
                inputLayoutFechaNac.error = null
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    // --- LÓGICA DE CARGA DE ENFERMEDADES (API) ---
    private fun cargarEnfermedades() {
        lifecycleScope.launch {
            try {
                enfermedadesRemotas = OracleRemoteDataSource.obtenerEnfermedades()

                val nombresEnfermedades = mutableListOf("Seleccionar enfermedad")
                nombresEnfermedades.addAll(enfermedadesRemotas.mapNotNull { it.nombreEnfermedad })

                val adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, nombresEnfermedades)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEnfermedades.adapter = adapter
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error cargando enfermedades: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Error al cargar catálogo de enfermedades.", Toast.LENGTH_LONG).show()
                val nombresEnfermedades = listOf("Error de carga")
                val adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, nombresEnfermedades)
                spinnerEnfermedades.adapter = adapter
            }
        }
    }

    // --- LÓGICA DE CARGA DE OBJETIVOS (API - Dinámico con RadioButtons) ---
    private fun cargarObjetivos() {
        lifecycleScope.launch {
            try {
                objetivosRemotos = OracleRemoteDataSource.obtenerObjetivos()

                rgObjetivos.removeAllViews()

                if (objetivosRemotos.isNotEmpty()) {
                    objetivosRemotos.forEachIndexed { index, objetivo ->
                        val radioButton = RadioButton(this@RegisterActivity).apply {
                            layoutParams = RadioGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 4.dpToPx(), 0, 4.dpToPx())
                            }

                            // Muestra el nombre/descripción del objetivo (CORREGIDO)
                            text = objetivo.nombreObjetivo ?: "Error: Contenido no disponible (ID: ${objetivo.idObjetivo})"

                            id = index + 100 // IDs únicos para el RadioGroup

                            // Aplicar el drawable selector de caja
                            setBackgroundResource(R.drawable.radio_button_selector)
                            // Ocultar el círculo nativo, dejando solo el fondo de caja
                            buttonDrawable = ContextCompat.getDrawable(context, android.R.color.transparent)

                            // Añadir padding interno
                            setPadding(32.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        rgObjetivos.addView(radioButton)

                        // Seleccionar el primero por defecto
                        if (index == 0) {
                            rgObjetivos.check(radioButton.id)
                        }
                    }
                } else {
                    val textView = TextView(this@RegisterActivity).apply {
                        text = "No hay objetivos disponibles."
                    }
                    rgObjetivos.addView(textView)
                }
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error cargando objetivos: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Error al cargar catálogo de objetivos: ${e.message}", Toast.LENGTH_LONG).show()
                val textView = TextView(this@RegisterActivity).apply {
                    text = "Error al cargar objetivos remotos."
                }
                rgObjetivos.addView(textView)
            }
        }
    }

    // Función de extensión para la conversión de DP a PX
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }


    // --- LÓGICA DE VALIDACIÓN ---

    private fun validarCampos(): Boolean {
        var isValid = true

        fun validateEmpty(editText: EditText, layout: TextInputLayout, fieldName: String): Boolean {
            return if (editText.text.isNullOrEmpty()) {
                layout.error = "El campo $fieldName no puede estar vacío."
                false
            } else {
                layout.error = null
                true
            }
        }

        // 1. Validar campos de texto vacíos
        isValid = validateEmpty(etNombre, inputLayoutNombre, "Nombre") && isValid
        isValid = validateEmpty(etEmail, inputLayoutEmail, "Correo") && isValid
        isValid = validateEmpty(etRut, inputLayoutRut, "RUT") && isValid
        isValid = validateEmpty(etDv, inputLayoutDv, "DV") && isValid
        isValid = validateEmpty(etAltura, inputLayoutAltura, "Altura") && isValid
        isValid = validateEmpty(etPeso, inputLayoutPeso, "Peso") && isValid
        isValid = validateEmpty(etFechaNac, inputLayoutFechaNac, "Fecha de nacimiento") && isValid
        isValid = validateEmpty(etPassword, inputLayoutPassword, "Contraseña") && isValid

        // 2. Validar RUT completo
        if (etRut.text.isNotEmpty() && etDv.text.isNotEmpty()) {
            if (!validarRut(etRut.text.toString().toLong(), etDv.text.toString())) {
                inputLayoutRut.error = "RUT y/o DV inválidos."
                isValid = false
            } else {
                inputLayoutRut.error = null
            }
        }

        // 3. Validar largo de Contraseña (mínimo 8)
        if (etPassword.text.toString().length < 8) {
            inputLayoutPassword.error = "La contraseña debe tener al menos 8 caracteres."
            isValid = false
        } else {
            inputLayoutPassword.error = null
        }

        // 4. Validar selección de Sexo (RadioGroup)
        if (rgSexo.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Debe seleccionar un Sexo.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // 5. Validar selección de Objetivo (RadioGroup)
        if (rgObjetivos.childCount > 0 && rgObjetivos.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Debe seleccionar un Objetivo.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    /**
     * Algoritmo del Módulo 11 para validar el RUT Chileno.
     */
    private fun validarRut(rut: Long, dv: String): Boolean {
        var rutTemp = rut

        var suma: Long = 0
        var multiplo: Long = 2

        while (rutTemp > 0) {
            suma += (rutTemp % 10) * multiplo
            rutTemp /= 10
            multiplo++
            if (multiplo == 8L) multiplo = 2L
        }

        val resto = suma % 11L
        val digitoVerificadorCalculado = 11L - resto

        val dvEsperado = when (digitoVerificadorCalculado) {
            11L -> "0"
            10L -> "K"
            else -> digitoVerificadorCalculado.toString()
        }

        return dv.uppercase() == dvEsperado
    }

    // --- LÓGICA DE REGISTRO ---

    private fun registrarUsuario() {
        val rut = etRut.text.toString().toLong()
        val dv = etDv.text.toString().uppercase()
        val nombre = etNombre.text.toString()
        val email = etEmail.text.toString()
        val altura = etAltura.text.toString().toInt()
        val peso = etPeso.text.toString().toInt()
        val password = etPassword.text.toString()

        val fechaNacStr = etFechaNac.text.toString()
        val fechaNacApi = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacStr)?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: return

        val genero = when (rgSexo.checkedRadioButtonId) {
            R.id.rbMasculino -> "Masculino"
            R.id.rbFemenino -> "Femenino"
            R.id.rbOtro -> "Otro"
            else -> ""
        }

        val idsEnfermedades = if (spinnerEnfermedades.selectedItemPosition > 0 && enfermedadesRemotas.isNotEmpty()) {
            listOf(enfermedadesRemotas[spinnerEnfermedades.selectedItemPosition - 1].idEnfermedad)
        } else {
            null
        }

        val idObjetivoSeleccionado: Long? = if (rgObjetivos.checkedRadioButtonId != -1) {
            val checkedId = rgObjetivos.checkedRadioButtonId
            objetivosRemotos.getOrNull(checkedId - 100)?.idObjetivo
        } else {
            null
        }

        val req = PacientePostRequest(
            rut = rut,
            dv = dv,
            nombre = nombre,
            email = email,
            fechaNac = fechaNacApi,
            genero = genero,
            altura = altura,
            pesoActual = peso,
            idMedico = 30L,
            password = password,
            enfermedades = idsEnfermedades,
            imageId = null
        )

        lifecycleScope.launch {
            try {
                // Paso 1: Registrar el paciente base
                val exito = OracleRemoteDataSource.crearPacienteRemoto(req)

                if (exito) {
                    // Si el registro base fue exitoso, intentamos obtener el ID del paciente recién creado por email
                    val pacienteNuevo = OracleRemoteDataSource.obtenerPacientePorEmail(req.email)
                    val nuevoId = pacienteNuevo?.idPaciente

                    if (nuevoId != null) {
                        Toast.makeText(this@RegisterActivity, "Registro exitoso. Inicia sesión.", Toast.LENGTH_LONG).show()

                        // Paso 2: Asociar el objetivo
                        if (idObjetivoSeleccionado != null) {
                            val objExito = OracleRemoteDataSource.asociarObjetivoPaciente(nuevoId, idObjetivoSeleccionado)
                            if (objExito) {
                                Log.i("RegisterActivity", "Objetivo $idObjetivoSeleccionado asociado exitosamente a ID $nuevoId.")
                            } else {
                                Log.e("RegisterActivity", "Fallo al asociar objetivo $idObjetivoSeleccionado a ID $nuevoId.")
                            }
                        }

                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registro exitoso, pero no se pudo recuperar el ID del paciente para asociar objetivos.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@RegisterActivity, "Fallo al registrar. El email/RUT podría estar en uso.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error de conexión/registro: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Error de conexión/registro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}