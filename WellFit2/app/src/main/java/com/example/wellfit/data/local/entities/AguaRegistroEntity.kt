package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agua_registros")
data class AguaRegistroEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val idPaciente: Long,

    val ml: Int,                 // 250 ml por vaso
    val timestamp: Long          // hora exacta
)
