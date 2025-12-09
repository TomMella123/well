package com.example.wellfit.data.repository

import com.example.wellfit.data.local.dao.DesafioDao
import com.example.wellfit.data.local.entities.DesafioEntity
import com.example.wellfit.data.local.entities.ObjPacEntity
import com.example.wellfit.data.local.entities.ObjetivoEntity

class DesafioRepository(
    private val desafioDao: DesafioDao
) {

    // Insertar desafío
    suspend fun insertDesafio(desafio: DesafioEntity): Long =
        desafioDao.insertDesafio(desafio)

    // Listar desafíos
    suspend fun getAllDesafios(): List<DesafioEntity> =
        desafioDao.getAllDesafios()

    // Insertar objetivo
    suspend fun insertObjetivo(objetivo: ObjetivoEntity): Long =
        desafioDao.insertObjetivo(objetivo)

    // Asignar objetivo a paciente
    suspend fun insertObjPac(objPacEntity: ObjPacEntity): Long =
        desafioDao.insertObjPac(objPacEntity)

    // Obtener objetivos de un paciente
    suspend fun getObjetivosByPaciente(idPaciente: Long): List<ObjetivoEntity> =
        desafioDao.getObjetivosByPaciente(idPaciente)
}
