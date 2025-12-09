// data/local/entities/DesafioEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "desafio")
data class DesafioEntity(
    @PrimaryKey(autoGenerate = true)
    val idDesafio: Long = 0L,
    val nombreDesafio: String,
    val descripcionDesafio: String?,
    val puntaje: Int,
    val idDificultad: Long?,
    val desafioImagenId: String?
)
