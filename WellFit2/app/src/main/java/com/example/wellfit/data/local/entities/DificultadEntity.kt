// data/local/entities/DificultadEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dificultad")
data class DificultadEntity(
    @PrimaryKey(autoGenerate = true)
    val idDificultad: Long = 0L,
    val nombreDificultad: String
)
