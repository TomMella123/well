// data/local/entities/PacienteEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paciente")
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    val idPaciente: Long = 0L,
    val rutPaciente: String,
    val dvPaciente: String,
    val nombrePaciente: String,
    val correoPaciente: String,
    val fechaNacimiento: String?,   // la puedes cambiar a Date + TypeConverter
    val generoPaciente: String?,
    val alturaPaciente: Double?,
    val pesoPaciente: Double?,
    val passHashPaciente: String,
    val passSaltPaciente: String?,
    val pacienteImagenId: String?   // ruta o id de imagen local
)
