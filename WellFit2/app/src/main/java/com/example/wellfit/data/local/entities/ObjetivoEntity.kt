// data/local/entities/ObjetivoEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "objetivo")
data class ObjetivoEntity(
    @PrimaryKey(autoGenerate = true)
    val idObjetivo: Long = 0L,
    val objetivo: String,
    val descripcionObjetivo: String?
)
