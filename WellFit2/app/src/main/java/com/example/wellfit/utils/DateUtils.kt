package com.example.wellfit.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DEFAULT_PATTERN = Constants.DATE_FORMAT_API
    private val DEFAULT_LOCALE = Locale("es", "CL")

    /**
     * Obtiene la fecha de hoy como String con el formato dado.
     * Por defecto: yyyy-MM-dd (para guardar en BD).
     */
    fun today(pattern: String = DEFAULT_PATTERN, locale: Locale = DEFAULT_LOCALE): String {
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(Date())
    }

    /**
     * Formatea un Date cualquiera a String.
     */
    fun format(
        date: Date,
        pattern: String = DEFAULT_PATTERN,
        locale: Locale = DEFAULT_LOCALE
    ): String {
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(date)
    }

    /**
     * Convierte un String de fecha a Date.
     * Devuelve null si el formato es inválido.
     */
    fun parse(
        dateString: String,
        pattern: String = DEFAULT_PATTERN,
        locale: Locale = DEFAULT_LOCALE
    ): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern, locale)
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convierte una fecha en String desde un formato origen a otro formato destino.
     * Si falla el parseo, devuelve el mismo string original.
     */
    fun reformatDate(
        dateString: String,
        fromPattern: String = DEFAULT_PATTERN,
        toPattern: String = Constants.DATE_FORMAT_DISPLAY,
        locale: Locale = DEFAULT_LOCALE
    ): String {
        val parsed = parse(dateString, fromPattern, locale) ?: return dateString
        return format(parsed, toPattern, locale)
    }

    /**
     * Calcula la edad a partir de una fecha de nacimiento en String.
     * Devuelve null si la fecha es inválida.
     */
    fun calculateAgeFromDateString(
        dobString: String,
        pattern: String = DEFAULT_PATTERN,
        locale: Locale = DEFAULT_LOCALE
    ): Int? {
        val dob = parse(dobString, pattern, locale) ?: return null

        val dobCal = Calendar.getInstance().apply { time = dob }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR)

        // Si aún no cumple años este año, restamos 1
        val todayDayOfYear = today.get(Calendar.DAY_OF_YEAR)
        val dobDayOfYear = dobCal.get(Calendar.DAY_OF_YEAR)
        if (todayDayOfYear < dobDayOfYear) {
            age--
        }

        return age
    }

    /**
     * Devuelve la diferencia en días entre dos fechas en String
     * (dateEnd - dateStart). Si algo falla, devuelve null.
     */
    fun daysBetween(
        dateStart: String,
        dateEnd: String,
        pattern: String = DEFAULT_PATTERN,
        locale: Locale = DEFAULT_LOCALE
    ): Long? {
        val start = parse(dateStart, pattern, locale) ?: return null
        val end = parse(dateEnd, pattern, locale) ?: return null

        val diffMillis = end.time - start.time
        return diffMillis / (1000L * 60L * 60L * 24L)
    }
}
