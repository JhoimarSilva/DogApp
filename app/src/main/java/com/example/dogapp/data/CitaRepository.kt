package com.example.dogapp.data

class CitaRepository(private val citaDao: CitaDao) {
    suspend fun insertarCita(cita: Cita) {
        citaDao.insertarCita(cita)
    }

    suspend fun obtenerTodas(): List<Cita> {
        return citaDao.obtenerTodas()
    }
}