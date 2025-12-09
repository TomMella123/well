package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.RecetaDao
import com.example.wellfit.data.local.entities.RecetaEnfermCrossRef
import com.example.wellfit.data.local.entities.RecetaEntity

class RecetaRepository(
    private val recetaDao: RecetaDao
) {

    // Insertar receta nueva
    suspend fun insertReceta(receta: RecetaEntity): Long =
        recetaDao.insertReceta(receta)

    // Listar todas las recetas
    suspend fun getAllRecetas(): List<RecetaEntity> =
        recetaDao.getAllRecetas()

    // Listar recetas según enfermedad (diabetes / HTA)
    suspend fun getRecetasByEnfermedad(idEnfermedad: Long): List<RecetaEntity> =
        recetaDao.getRecetasByEnfermedad(idEnfermedad)

    // Vincular receta con enfermedad (muchos a muchos)
    suspend fun vincularRecetaEnfermedad(idReceta: Long, idEnfermedad: Long) =
        recetaDao.insertRecetaEnfermCrossRef(
            RecetaEnfermCrossRef(idReceta, idEnfermedad)
        )
}
