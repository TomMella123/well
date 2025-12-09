// data/local/dao/PacienteDao.kt
package com.example.wellfit.data.local.dao

import androidx.room.*
import com.example.wellfit.data.local.entities.EnfermedadEntity
import com.example.wellfit.data.local.entities.PacienteEnfermedadCrossRef
import com.example.wellfit.data.local.entities.PacienteEntity

@Dao
interface PacienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: PacienteEntity): Long

    @Query("SELECT * FROM paciente WHERE correoPaciente = :email LIMIT 1")
    suspend fun getPacienteByEmail(email: String): PacienteEntity?

    @Query("SELECT * FROM paciente WHERE idPaciente = :id LIMIT 1")
    suspend fun getPacienteById(id: Long): PacienteEntity?

    // Enfermedades
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnfermedad(enfermedad: EnfermedadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacienteEnfermedadCrossRef(cross: PacienteEnfermedadCrossRef)

    @Transaction
    @Query(
        """
        SELECT e.* 
        FROM enfermedad e
        INNER JOIN pac_enf pe ON e.idEnfermedad = pe.idEnfermedad
        WHERE pe.idPaciente = :idPaciente
        """
    )
    suspend fun getEnfermedadesByPaciente(idPaciente: Long): List<EnfermedadEntity>
}
