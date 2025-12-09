// data/local/entities/ObjPacEntity.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obj_pac")
data class ObjPacEntity(
    @PrimaryKey(autoGenerate = true)
    val idObjPac: Long = 0L,
    val idPaciente: Long,
    val idObjetivo: Long
)
