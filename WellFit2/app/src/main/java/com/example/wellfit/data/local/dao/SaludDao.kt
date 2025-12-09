package com.example.wellfit.data.local.dao

import androidx.room.*
import com.example.wellfit.data.local.entities.AguaRegistroEntity
import com.example.wellfit.data.local.entities.HistorialPesoEntity
import com.example.wellfit.data.local.entities.UserHealthDataEntity

@Dao
interface SaludDao {

    // ==============================
    //  Indicadores generales
    // ==============================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserHealthData(data: UserHealthDataEntity): Long

    @Query(
        """
        SELECT * 
        FROM user_health_data 
        WHERE idPaciente = :idPaciente 
        ORDER BY fechaData DESC
        """
    )
    suspend fun getHealthDataByPaciente(idPaciente: Long): List<UserHealthDataEntity>

    // Último registro de glucosa (para el dashboard)
    @Query(
        """
        SELECT * 
        FROM user_health_data 
        WHERE glucosaSangre IS NOT NULL 
        ORDER BY fechaData DESC, idData DESC 
        LIMIT 1
        """
    )
    suspend fun getUltimoRegistroGlucosa(): UserHealthDataEntity?

    // 🔥 Último registro de presión arterial (para el dashboard)
    @Query(
        """
        SELECT * 
        FROM user_health_data 
        WHERE presionSistolica IS NOT NULL
          AND presionDiastolica IS NOT NULL
          AND idPaciente = :idPaciente
        ORDER BY fechaData DESC, idData DESC 
        LIMIT 1
        """
    )
    suspend fun getUltimoRegistroPresion(idPaciente: Long): UserHealthDataEntity?

    // ==============================
    //  Agua / Hidratación
    // ==============================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAguaRegistro(registro: AguaRegistroEntity): Long

    @Query(
        """
        SELECT * 
        FROM agua_registros 
        WHERE idPaciente = :idPaciente 
        ORDER BY timestamp ASC
        """
    )
    suspend fun getHistorialAgua(idPaciente: Long): List<AguaRegistroEntity>

    @Query("DELETE FROM agua_registros WHERE id = :id")
    suspend fun eliminarRegistroAgua(id: Long)

    // ==============================
    //  Historial de peso
    // ==============================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorialPeso(historial: HistorialPesoEntity): Long

    @Query(
        """
        SELECT * 
        FROM historial_peso 
        WHERE idPaciente = :idPaciente 
        ORDER BY fechaCambio DESC
        """
    )
    suspend fun getHistorialPeso(idPaciente: Long): List<HistorialPesoEntity>

    // ==============================
    //  Pendientes de sincronizar
    // ==============================

    @Query("SELECT * FROM user_health_data WHERE pendingSync = 1")
    suspend fun getHealthDataPendienteSync(): List<UserHealthDataEntity>

    @Query("UPDATE user_health_data SET pendingSync = 0 WHERE idData IN (:ids)")
    suspend fun marcarHealthDataSincronizada(ids: List<Long>)

    @Query("SELECT * FROM historial_peso WHERE pendingSync = 1")
    suspend fun getHistorialPesoPendienteSync(): List<HistorialPesoEntity>

    @Query("UPDATE historial_peso SET pendingSync = 0 WHERE idHistorialPeso IN (:ids)")
    suspend fun marcarHistorialPesoSincronizado(ids: List<Long>)
}
