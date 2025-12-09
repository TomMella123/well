package com.example.wellfit.data.local.dto

data class PacienteDto(
    val id: Int? = null,
    val rut: String,
    val dv: String,
    val nombre: String,
    val correo: String,
    val fechaNacimiento: String,
    val sexo: String,
    val altura: Int,
    val peso: Int,
    val password: String
)
