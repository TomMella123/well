package com.example.wellfit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wellfit.data.local.dao.*
import com.example.wellfit.data.local.entities.*

@Database(
    entities = [
        // Paciente + enfermedades
        PacienteEntity::class,
        EnfermedadEntity::class,
        PacienteEnfermedadCrossRef::class,

        // Salud
        UserHealthDataEntity::class,
        HistorialPesoEntity::class,
        AguaRegistroEntity::class,

        // Ejercicio / desafíos / recetas
        DesafioEntity::class,
        DificultadEntity::class,
        EjercicioEntity::class,
        ObjetivoEntity::class,
        ObjPacEntity::class,
        RecetaEntity::class,
        RecetaEnfermCrossRef::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pacienteDao(): PacienteDao
    abstract fun saludDao(): SaludDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun desafioDao(): DesafioDao
    abstract fun recetaDao(): RecetaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellfit_db_v3"
                )
                    .fallbackToDestructiveMigration()   // 👈 esto debe estar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
