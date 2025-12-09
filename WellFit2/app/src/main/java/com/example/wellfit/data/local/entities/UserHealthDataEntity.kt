// data/local/entities/UserHealthDataEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_health_data")
data class UserHealthDataEntity(

    @PrimaryKey(autoGenerate = true)
    val idData: Long = 0L,

    // fecha del registro (formato libre, pero consistente)
    val fechaData: String,

    // valores de salud (pueden ser null si no aplica)
    val presionSistolica: Int?,
    val presionDiastolica: Int?,
    val glucosaSangre: Int?,
    val aguaVasos: Int?,
    val pasos: Int?,

    // FK al paciente
    val idPaciente: Long,

    // 👇 ESTA ES LA COLUMNA QUE FALTABA EN LA BD
    val pendingSync: Boolean = true
)
