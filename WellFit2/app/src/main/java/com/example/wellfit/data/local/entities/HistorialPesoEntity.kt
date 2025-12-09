// data/local/entities/HistorialPesoEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_peso")
data class HistorialPesoEntity(

    @PrimaryKey(autoGenerate = true)
    val idHistorialPeso: Long = 0L,

    val fechaCambio: String,
    val peso: Double,
    val idPaciente: Long,

    // 👇 igual que en UserHealthDataEntity
    val pendingSync: Boolean = true
)
