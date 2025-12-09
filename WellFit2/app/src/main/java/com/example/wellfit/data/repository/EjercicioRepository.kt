package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.EjercicioDao
import com.example.wellfit.data.local.entities.DificultadEntity
import com.example.wellfit.data.local.entities.EjercicioEntity

class EjercicioRepository(
    private val ejercicioDao: EjercicioDao
) {

    // Insertar ejercicio
    suspend fun insertEjercicio(ejercicio: EjercicioEntity): Long =
        ejercicioDao.insertEjercicio(ejercicio)

    // Obtener lista de ejercicios
    suspend fun getEjercicios(): List<EjercicioEntity> =
        ejercicioDao.getAllEjercicios()

    // Insertar dificultad
    suspend fun insertDificultad(dificultad: DificultadEntity): Long =
        ejercicioDao.insertDificultad(dificultad)

    // Obtener dificultades
    suspend fun getDificultades(): List<DificultadEntity> =
        ejercicioDao.getAllDificultades()
}
