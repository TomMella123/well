package com.example.wellfit.ui.perfil

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.wellfit.R
import com.example.wellfit.core.BaseActivity
import com.example.wellfit.data.local.UserKeys
import com.example.wellfit.data.local.UserPrefs
import com.example.wellfit.data.remote.AsociacionEnfermedadRemota
import com.example.wellfit.data.remote.EnfermedadRemota
import com.example.wellfit.data.remote.OracleRemoteDataSource
import com.example.wellfit.data.remote.PacienteRemoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class PerfilActivity : BaseActivity() {

    private lateinit var prefs: UserPrefs
    private var idPaciente: Long = 0L
    private var emailPaciente: String? = null

    // Referencias de UI
    private lateinit var txtNombre: TextView
    private lateinit var txtCorreo: TextView
    private lateinit var txtRut: TextView
    private lateinit var txtSexo: TextView
    private lateinit var txtFecha: TextView
    private lateinit var txtAltura: TextView
    private lateinit var txtPeso: TextView
    private lateinit var txtEnfer: TextView
    private lateinit var imgPerfil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        prefs = UserPrefs(this)
        idPaciente = prefs.getString("idPaciente")?.toLongOrNull() ?: 0L
        emailPaciente = prefs.getString(UserKeys.CORREO)

        inicializarVistas()

        // Muestra datos de caché primero
        cargarDatosDesdeCache()

        // Carga los datos frescos de la BD
        cargarDatosDesdeBaseDeDatos(idPaciente, emailPaciente)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun inicializarVistas() {
        imgPerfil = findViewById(R.id.imgPerfil)
        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtRut = findViewById(R.id.txtRut)
        txtSexo = findViewById(R.id.txtSexo)
        txtFecha = findViewById(R.id.txtFecha)
        txtAltura = findViewById(R.id.txtAltura)
        txtPeso = findViewById(R.id.txtPeso)
        txtEnfer = findViewById(R.id.txtEnfermedades)
    }

    private fun cargarDatosDesdeCache() {
        val rutRaw = prefs.getString(UserKeys.RUT) ?: ""
        val dv = prefs.getString(UserKeys.DV) ?: ""
        val alturaCm = prefs.getDouble(UserKeys.ALTURA)
        val pesoKg = prefs.getDouble(UserKeys.PESO)

        txtNombre.text = prefs.getString(UserKeys.NOMBRE) ?: "Cargando..."
        txtCorreo.text = prefs.getString(UserKeys.CORREO) ?: "Cargando..."
        txtSexo.text = prefs.getString(UserKeys.SEXO) ?: "No especificado"
        txtFecha.text = prefs.getString(UserKeys.FECHA) ?: "--/--/----"

        // Aplicamos el formato inicial usando los datos de caché
        aplicarFormato(
            rutRaw = rutRaw,
            dv = dv,
            alturaCm = alturaCm,
            pesoKg = pesoKg,
            enfermedadesDelUsuario = mutableListOf()
        )

        val bitmapFoto = prefs.getImage(UserKeys.IMAGEN)
        if (bitmapFoto != null) {
            imgPerfil.setImageBitmap(bitmapFoto)
        }
    }

    // Realiza las llamadas GET a la base de datos remota
    private fun cargarDatosDesdeBaseDeDatos(id: Long, email: String?) {
        if (id == 0L || email.isNullOrEmpty()) {
            Toast.makeText(this, "ID o Email no disponibles para consulta remota.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            var user: PacienteRemoto? = null
            var asociaciones: List<AsociacionEnfermedadRemota> = emptyList()
            var catalogoCompleto: List<EnfermedadRemota> = emptyList()
            var enfermedadesUsuario: MutableList<String> = mutableListOf()

            try {
                // 1. LLAMADA GET: Perfil del usuario
                user = OracleRemoteDataSource.obtenerPacientePorEmail(email)

                // Si el perfil se obtuvo
                if (user != null) {

                    // 🛠️ CORRECCIÓN: Aplicar los datos básicos del perfil inmediatamente en el hilo principal
                    withContext(Dispatchers.Main) {
                        txtNombre.text = user.nombre ?: "Sin nombre"
                        txtCorreo.text = user.email ?: "Sin correo"
                        txtSexo.text = user.genero ?: "No especificado"
                        txtFecha.text = user.fechaNac ?: "--/--/----"

                        // Aplicamos el formato (RUT, Altura, Peso) sin esperar las enfermedades
                        aplicarFormato(
                            rutRaw = user.rutPaciente?.toString() ?: "",
                            dv = user.dvPaciente ?: prefs.getString(UserKeys.DV) ?: "",
                            alturaCm = user.altura?.toDouble() ?: 0.0,
                            pesoKg = user.pesoActual?.toDouble() ?: 0.0,
                            enfermedadesDelUsuario = emptyList<String>().toMutableList() // Se deja vacío temporalmente
                        )
                    }

                    // 2. LLAMADA GET: Asociaciones de enfermedades (Puede ser lento, se ejecuta en IO)
                    asociaciones = OracleRemoteDataSource.obtenerAsociacionesEnfermedadPaciente(id)

                    // 3. LLAMADA GET: Catálogo completo de nombres de enfermedades (Puede ser lento, se ejecuta en IO)
                    catalogoCompleto = OracleRemoteDataSource.obtenerEnfermedades()

                    // Cruce de datos: De IDs a Nombres de Enfermedades (Puede ser intensivo)
                    enfermedadesUsuario = obtenerNombresEnfermedades(asociaciones, catalogoCompleto)

                    // 🛠️ CORRECCIÓN: Actualizar solo las enfermedades una vez que se obtienen
                    withContext(Dispatchers.Main) {
                        aplicarFormato(
                            rutRaw = user.rutPaciente?.toString() ?: "",
                            dv = user.dvPaciente ?: prefs.getString(UserKeys.DV) ?: "",
                            alturaCm = user.altura?.toDouble() ?: 0.0,
                            pesoKg = user.pesoActual?.toDouble() ?: 0.0,
                            enfermedadesDelUsuario = enfermedadesUsuario // Ahora con los datos completos
                        )
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PerfilActivity, "Usuario no encontrado en la base de datos remota. Mostrando caché.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                // Si hay un error, notificamos y seguimos mostrando la caché.
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error al cargar datos remotos: ${e.message}. Mostrando caché.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Cruza la lista de IDs de asociación con el catálogo de nombres
    private fun obtenerNombresEnfermedades(
        asociaciones: List<AsociacionEnfermedadRemota>,
        catalogoCompleto: List<EnfermedadRemota>
    ): MutableList<String> {
        val mapaNombres = catalogoCompleto.associate { it.idEnfermedad to it.nombreEnfermedad }

        val nombresEncontrados = asociaciones.mapNotNull { asociacion ->
            mapaNombres[asociacion.idEnfermedad]
        }.filterNotNull().toMutableList()

        return nombresEncontrados
    }


    // Aplica todo el formato de presentación a los TextViews
    private fun aplicarFormato(
        rutRaw: String, dv: String, alturaCm: Double, pesoKg: Double,
        enfermedadesDelUsuario: MutableList<String>
    ) {
        // --- 1. RUT (Ej: 12.345.678-K) ---
        if (rutRaw.isNotEmpty()) {
            txtRut.text = "${formatearRutConPuntos(rutRaw)}-$dv"
        } else {
            txtRut.text = "Sin RUT"
        }

        // --- 2. ALTURA (Ej: 1,84 M) ---
        if (alturaCm > 0) {
            val metros = alturaCm / 100.0
            val df = DecimalFormat("0.00")
            txtAltura.text = "${df.format(metros).replace('.', ',')} M"
        } else {
            txtAltura.text = "-- M"
        }

        // --- 3. PESO (Ej: 80 Kg) ---
        txtPeso.text = if (pesoKg > 0) "$pesoKg Kg" else "-- Kg"

        // --- 4. ENFERMEDADES (Ej: Diabetes, Hipertensión) ---
        txtEnfer.text = if (enfermedadesDelUsuario.isNotEmpty()) {
            txtEnfer.setTextColor(resources.getColor(R.color.black))
            enfermedadesDelUsuario.joinToString(", ")
        } else {
            // El color 'gray' no está definido aquí, asumimos que R.color.gray existe o usamos un fallback
            txtEnfer.setTextColor(resources.getColor(R.color.blue))
            "Ninguna"
        }
    }

    // Función auxiliar para poner los puntos al RUT (12345678 -> 12.345.678)
    private fun formatearRutConPuntos(rut: String): String {
        return try {
            rut.reversed().chunked(3).joinToString(".").reversed()
        } catch (e: Exception) {
            rut
        }
    }
}