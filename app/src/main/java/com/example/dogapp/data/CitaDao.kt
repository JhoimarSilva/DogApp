package com.example.dogapp.data
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CitaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCita(cita: Cita)

    @Query("SELECT * FROM citas")
    suspend fun obtenerTodas(): List<Cita>
}
