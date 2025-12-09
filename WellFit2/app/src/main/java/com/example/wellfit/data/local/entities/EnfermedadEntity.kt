// data/local/entities/EnfermedadEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enfermedad")
data class EnfermedadEntity(
    @PrimaryKey(autoGenerate = true)
    val idEnfermedad: Long = 0L,
    val nombreEnfermedad: String,
    val descripcionEnfermedad: String?
)
