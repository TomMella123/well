// data/local/entities/EjercicioEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicio")
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val idEjercicio: Long = 0L,
    val nombreEjercicio: String,
    val descripcionEjercicio: String?,
    val pasosEjercicio: String?,
    val idDificultad: Long?,
    val ejercicioImagenId: String?
)
