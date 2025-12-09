package com.example.wellfit.ui.perfil

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.wellfit.R
import com.example.wellfit.data.local.UserPrefs

class EditProfileActivity : ComponentActivity() {

    private lateinit var prefs: UserPrefs

    // Views
    private lateinit var imgEditFoto: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var btnCambiarFoto: ImageView
    private lateinit var btnGuardar: Button

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etRut: EditText
    private lateinit var etDV: EditText
    private lateinit var etFecha: EditText
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText

    private lateinit var rbHombre: RadioButton
    private lateinit var rbMujer: RadioButton
    private lateinit var rbOtro: RadioButton

    private lateinit var chkDiabetes: CheckBox
    private lateinit var chkHipertension: CheckBox

    private val PICK_IMAGE = 100
    private var nuevaImagen: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        prefs = UserPrefs(this)

        initViews()
        cargarDatos()
        configurarEventos()
    }

    private fun initViews() {
        imgEditFoto = findViewById(R.id.imgEditFoto)
        btnBack = findViewById(R.id.btnBack)
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto)
        btnGuardar = findViewById(R.id.btnGuardar)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etRut = findViewById(R.id.etRut)
        etDV = findViewById(R.id.etDV)
        etFecha = findViewById(R.id.etFecha)
        etAltura = findViewById(R.id.etAltura)
        etPeso = findViewById(R.id.etPeso)

        rbHombre = findViewById(R.id.rbHombre)
        rbMujer = findViewById(R.id.rbMujer)
        rbOtro = findViewById(R.id.rbOtro)

        chkDiabetes = findViewById(R.id.chkDiabetes)
        chkHipertension = findViewById(R.id.chkHipertension)
    }

    private fun cargarDatos() {
        etNombre.setText(prefs.getString("nombre") ?: "")
        etCorreo.setText(prefs.getString("correo") ?: "")
        etRut.setText(prefs.getString("rut") ?: "")
        etDV.setText(prefs.getString("dv") ?: "")
        etFecha.setText(prefs.getString("fecha") ?: "")
        etAltura.setText(prefs.getDouble("altura").toString())
        etPeso.setText(prefs.getDouble("peso").toString())

        when (prefs.getString("sexo")) {
            "Masculino" -> rbHombre.isChecked = true
            "Femenino" -> rbMujer.isChecked = true
            "Otro" -> rbOtro.isChecked = true
        }

        chkDiabetes.isChecked = prefs.getBoolean("diabetes")
        chkHipertension.isChecked = prefs.getBoolean("hipertension")

        val foto = prefs.getImage("imagen")
        if (foto != null) imgEditFoto.setImageBitmap(foto)
    }

    private fun configurarEventos() {

        // Volver atrás
        btnBack.setOnClickListener { finish() }

        // Abrir galería
        btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Guardar cambios
        btnGuardar.setOnClickListener {
            mostrarConfirmacion()
        }
    }

    private fun mostrarConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar cambios")
            .setMessage("¿Deseas guardar los cambios realizados?")
            .setPositiveButton("Guardar") { _, _ ->
                guardarCambios()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarCambios() {
        val sexoSeleccionado = when {
            rbHombre.isChecked -> "Masculino"
            rbMujer.isChecked -> "Femenino"
            else -> "Otro"
        }

        prefs.saveString("nombre", etNombre.text.toString())
        prefs.saveString("correo", etCorreo.text.toString())
        prefs.saveString("rut", etRut.text.toString())
        prefs.saveString("dv", etDV.text.toString())
        prefs.saveString("fecha", etFecha.text.toString())

        val altura = etAltura.text.toString().toDoubleOrNull() ?: 0.0
        prefs.saveDouble("altura", altura)

        val peso = etPeso.text.toString().toDoubleOrNull() ?: 0.0
        prefs.saveDouble("peso", peso)

        prefs.saveString("sexo", sexoSeleccionado)

        prefs.saveBoolean("diabetes", chkDiabetes.isChecked)
        prefs.saveBoolean("hipertension", chkHipertension.isChecked)

        prefs.saveImage("imagen", nuevaImagen)

        Toast.makeText(this, "Cambios guardados.", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                nuevaImagen = bitmap
                imgEditFoto.setImageBitmap(bitmap)
            }
        }
    }
}
