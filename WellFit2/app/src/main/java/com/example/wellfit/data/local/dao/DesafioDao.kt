// data/local/dao/DesafioDao.kt
package com.example.wellfit.data.local.dao

import androidx.room.*
import com.example.wellfit.data.local.entities.*

@Dao
interface DesafioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesafio(desafio: DesafioEntity): Long

    @Query("SELECT * FROM desafio")
    suspend fun getAllDesafios(): List<DesafioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjetivo(objetivo: ObjetivoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjPac(objPac: ObjPacEntity): Long

    @Transaction
    @Query("""
        SELECT o.* FROM objetivo o
        INNER JOIN obj_pac op ON o.idObjetivo = op.idObjetivo
        WHERE op.idPaciente = :idPaciente
    """)
    suspend fun getObjetivosByPaciente(idPaciente: Long): List<ObjetivoEntity>
}
