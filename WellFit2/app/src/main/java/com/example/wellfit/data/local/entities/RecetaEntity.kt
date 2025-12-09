// data/local/entities/RecetaEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receta")
data class RecetaEntity(
    @PrimaryKey(autoGenerate = true)
    val idReceta: Long = 0L,
    val nombreReceta: String,
    val descripcionReceta: String?,
    val pasosReceta: String?,
    val idDificultad: Long?,
    val recetaImagenId: String?
)
