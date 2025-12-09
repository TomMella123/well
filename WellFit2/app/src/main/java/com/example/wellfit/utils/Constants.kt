// app/src/main/java/com/example/wellfit/utils/Constants.kt
package com.example.wellfit.utils

object Constants {

    // --- Base de datos ---
    const val DB_NAME = "wellfit_db"

    // --- Formatos de fecha ---
    // Formato base para guardar en la BD / Room
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    // Formato más amigable para mostrar al usuario
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    // Fecha + hora (si lo necesitas más adelante)
    const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"

    // --- Extras para Intents ---
    const val EXTRA_PACIENTE_ID = "extra_paciente_id"
    const val EXTRA_OBJETIVO_ID = "extra_objetivo_id"
    const val EXTRA_RECETA_ID = "extra_receta_id"
    const val EXTRA_EJERCICIO_ID = "extra_ejercicio_id"
    const val EXTRA_DESAFIO_ID = "extra_desafio_id"

    // --- Salud / Hidratación ---
    // Meta default de vasos de agua al día
    const val DEFAULT_WATER_GOAL_VASOS = 8

    // --- Otros ---
    const val EMPTY_STRING = ""
}
