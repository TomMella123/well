package com.example.wellfit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wellfit.ui.login.LoginActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Puedes usar un layout si quieres, pero no es obligatorio.
        // setContentView(R.layout.activity_main)

        // Ir directo a la pantalla de login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
