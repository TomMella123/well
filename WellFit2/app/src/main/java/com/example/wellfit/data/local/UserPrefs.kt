package com.example.wellfit.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class UserPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("WELLFIT_USER_DATA", Context.MODE_PRIVATE)

    // ============================
    //  STRINGS
    // ============================

    fun saveString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String? = null): String? {
        return prefs.getString(key, default)
    }

    // ============================
    //  DOUBLE (guardado como Long)
    // ============================

    fun saveDouble(key: String, value: Double?) {
        if (value == null) {
            prefs.edit().remove(key).apply()
        } else {
            val bits = java.lang.Double.doubleToRawLongBits(value)
            prefs.edit().putLong(key, bits).apply()
        }
    }

    fun getDouble(key: String, default: Double = 0.0): Double {
        val hasKey = prefs.contains(key)
        if (!hasKey) return default
        val bits = prefs.getLong(key, java.lang.Double.doubleToRawLongBits(default))
        return java.lang.Double.longBitsToDouble(bits)
    }

    // ============================
    //  BOOLEAN
    // ============================

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    // ============================
    //  LONG (id de usuario, etc.)
    // ============================

    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return prefs.getLong(key, default)
    }

    // Helpers específicos para el id de usuario logueado
    companion object {
        private const val KEY_USER_ID = "user_id_logged"
    }

    fun saveLoggedUserId(id: Long) {
        saveLong(KEY_USER_ID, id)
    }

    fun getLoggedUserId(): Long? {
        val v = getLong(KEY_USER_ID, -1L)
        return if (v == -1L) null else v
    }

    // ============================
    //  IMAGEN (Bitmap <-> Base64)
    // ============================

    fun saveImage(key: String, bitmap: Bitmap?) {
        if (bitmap == null) {
            prefs.edit().remove(key).apply()
            return
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        prefs.edit().putString(key, base64).apply()
    }

    fun getImage(key: String): Bitmap? {
        val encoded = prefs.getString(key, null) ?: return null
        return try {
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    // ============================
    //  LIMPIAR TODO
    // ============================

    fun clear() {
        prefs.edit().clear().apply()
    }
}
