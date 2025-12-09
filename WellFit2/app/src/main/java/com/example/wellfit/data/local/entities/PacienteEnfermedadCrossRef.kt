// data/local/entities/PacienteEnfermedadCrossRef.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "pac_enf",
    primaryKeys = ["idPaciente", "idEnfermedad"]
)
data class PacienteEnfermedadCrossRef(
    val idPaciente: Long,
    val idEnfermedad: Long
)
