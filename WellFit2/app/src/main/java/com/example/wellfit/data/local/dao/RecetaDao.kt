// data/local/dao/RecetaDao.kt
package com.example.wellfit.data.local.dao

import androidx.room.*
import com.example.wellfit.data.local.entities.*

@Dao
interface RecetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceta(receta: RecetaEntity): Long

    @Query("SELECT * FROM receta")
    suspend fun getAllRecetas(): List<RecetaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecetaEnfermCrossRef(cross: RecetaEnfermCrossRef)

    @Transaction
    @Query("""
        SELECT r.* FROM receta r
        INNER JOIN receta_enferm re ON r.idReceta = re.idReceta
        WHERE re.idEnfermedad = :idEnfermedad
    """)
    suspend fun getRecetasByEnfermedad(idEnfermedad: Long): List<RecetaEntity>
}
