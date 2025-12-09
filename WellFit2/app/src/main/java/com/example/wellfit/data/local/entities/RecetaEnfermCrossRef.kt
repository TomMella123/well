// data/local/entities/RecetaEnfermCrossRef.kt
package com.example.wellfit.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "receta_enferm",
    primaryKeys = ["idReceta", "idEnfermedad"]
)
data class RecetaEnfermCrossRef(
    val idReceta: Long,
    val idEnfermedad: Long
)
