package com.example.wellfit.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

// Nombre único para todas las preferencias
private const val PREFS_NAME = "wellfit_prefs"

// --------------------------------------------------------------------
// Helpers internos
// --------------------------------------------------------------------
private fun prefs(context: Context) =
    context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

// --------------------------------------------------------------------
// GETTERS
// --------------------------------------------------------------------

/**
 * Obtiene un String guardado en SharedPreferences.
 */
fun getString(context: Context, key: String, defaultValue: String = ""): String {
    return prefs(context).getString(key, defaultValue) ?: defaultValue
}

/**
 * Obtiene un Double guardado como Long (bits) en SharedPreferences.
 */
fun getDouble(context: Context, key: String, defaultValue: Double = 0.0): Double {
    val longValue = prefs(context).getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue))
    return java.lang.Double.longBitsToDouble(longValue)
}

/**
 * Obtiene un Boolean guardado en SharedPreferences.
 */
fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
    return prefs(context).getBoolean(key, defaultValue)
}

/**
 * Obtiene una imagen guardada en Base64 y la convierte a Bitmap.
 * Si no hay nada guardado, devuelve null.
 */
fun getImage(context: Context, key: String): Bitmap? {
    val base64 = prefs(context).getString(key, null) ?: return null
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

// --------------------------------------------------------------------
// SETTERS
// --------------------------------------------------------------------

/**
 * Guarda un String en SharedPreferences.
 */
fun saveString(context: Context, key: String, value: String) {
    prefs(context).edit().putString(key, value).apply()
}

/**
 * Guarda un Double en SharedPreferences (como Long de bits).
 */
fun saveDouble(context: Context, key: String, value: Double) {
    val longValue = java.lang.Double.doubleToRawLongBits(value)
    prefs(context).edit().putLong(key, longValue).apply()
}

/**
 * Guarda un Boolean en SharedPreferences.
 */
fun saveBoolean(context: Context, key: String, value: Boolean) {
    prefs(context).edit().putBoolean(key, value).apply()
}

/**
 * Guarda un Bitmap en SharedPreferences, convirtiéndolo a Base64.
 * OJO: esto es para imágenes pequeñas (foto de perfil, iconos, etc.)
 */
fun saveImage(context: Context, key: String, bitmap: Bitmap) {
    try {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        prefs(context).edit().putString(key, base64).apply()
    } catch (_: Exception) {
        // si falla, simplemente no guarda nada
    }
}
