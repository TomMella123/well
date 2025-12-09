package com.example.wellfit.ui.perfil

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserKeys
import com.example.wellfit.data.local.UserPrefs

class PerfilActivity : BaseActivity() {

    private lateinit var prefs: UserPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        prefs = UserPrefs(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val imgPerfil = findViewById<ImageView>(R.id.imgPerfil)

        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtCorreo = findViewById<TextView>(R.id.txtCorreo)
        val txtRut = findViewById<TextView>(R.id.txtRut)
        val txtSexo = findViewById<TextView>(R.id.txtSexo)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val txtAltura = findViewById<TextView>(R.id.txtAltura)
        val txtPeso = findViewById<TextView>(R.id.txtPeso)
        val txtEnfer = findViewById<TextView>(R.id.txtEnfermedades)

        // Cargar datos
        txtNombre.text = prefs.getString(UserKeys.NOMBRE)
        txtCorreo.text = prefs.getString(UserKeys.CORREO)
        txtRut.text = prefs.getString(UserKeys.RUT) + "-" + prefs.getString(UserKeys.DV)
        txtSexo.text = prefs.getString(UserKeys.SEXO)
        txtFecha.text = prefs.getString(UserKeys.FECHA)
        txtAltura.text = "${prefs.getDouble(UserKeys.ALTURA)} cm"
        txtPeso.text = "${prefs.getDouble(UserKeys.PESO)} kg"

        val enfermedades = mutableListOf<String>()
        if (prefs.getBoolean(UserKeys.DIABETES)) enfermedades.add("Diabetes")
        if (prefs.getBoolean(UserKeys.HIPERTENSION)) enfermedades.add("Hipertensión")
        txtEnfer.text = enfermedades.joinToString(", ")

        // Foto
        val bmp = prefs.getImage(UserKeys.IMAGEN)
        if (bmp != null) imgPerfil.setImageBitmap(bmp)

        btnBack.setOnClickListener { finish() }
    }
}
