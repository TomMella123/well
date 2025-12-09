// data/local/dao/EjercicioDao.kt
package com.example.wellfit.data.local.dao

import androidx.room.*
import com.example.wellfit.data.local.entities.*

@Dao
interface EjercicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEjercicio(ejercicio: EjercicioEntity): Long

    @Query("SELECT * FROM ejercicio")
    suspend fun getAllEjercicios(): List<EjercicioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDificultad(dificultad: DificultadEntity): Long

    @Query("SELECT * FROM dificultad")
    suspend fun getAllDificultades(): List<DificultadEntity>
}
