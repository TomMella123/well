package com.example.wellfit.data.local.dto

import com.example.wellfit.data.local.entities.HistorialPesoEntity
import com.example.wellfit.data.local.entities.UserHealthDataEntity

// =====================================
//   DTOs que viajan al backend
// =====================================

data class UserHealthDataDto(
    val fechaData: String,
    val presionSistolica: Int?,
    val presionDiastolica: Int?,
    val glucosaSangre: Int?,
    val aguaVasos: Int?,
    val pasos: Int?,
    val idPaciente: Long
)

data class HistorialPesoDto(
    val fechaCambio: String,
    val peso: Double,
    val idPaciente: Long
)

// =====================================
//   Mappers Entity -> DTO
// =====================================

fun UserHealthDataEntity.toDto(): UserHealthDataDto =
    UserHealthDataDto(
        fechaData = fechaData,
        presionSistolica = presionSistolica,
        presionDiastolica = presionDiastolica,
        glucosaSangre = glucosaSangre,
        aguaVasos = aguaVasos,
        pasos = pasos,
        idPaciente = idPaciente
    )

fun HistorialPesoEntity.toDto(): HistorialPesoDto =
    HistorialPesoDto(
        fechaCambio = fechaCambio,
        peso = peso,
        idPaciente = idPaciente
    )
