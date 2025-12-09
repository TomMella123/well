package com.example.wellfit.data.remote.oracle

import com.example.wellfit.data.local.dto.PacienteDto
import com.google.gson.JsonObject
import retrofit2.http.*

interface OracleApiService {

    @POST("ords/wellfit_admin/open-api-catalog/v1/gestion/")
    suspend fun crearUsuario(
        @Header("Authorization") authHeader: String, // "Bearer xxx"
        @Body usuario: PacienteDto
    ): JsonObject

    // ================
    // DESAFIO
    // ================
    @GET("ords/wellfit_admin/v1/gestion/desafio")
    suspend fun getDesafios(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>

    @GET("ords/wellfit_admin/v1/gestion/desafio/{id}")
    suspend fun getDesafioById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): JsonObject

    // ================
    // EJERCICIO
    // ================
    @GET("ords/wellfit_admin/v1/gestion/ejercicio")
    suspend fun getEjercicios(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>

    @GET("ords/wellfit_admin/v1/gestion/ejercicio/{id}")
    suspend fun getEjercicioById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): JsonObject

    // ================
    // RECETA
    // ================
    @GET("ords/wellfit_admin/v1/gestion/receta")
    suspend fun getRecetas(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>

    @GET("ords/wellfit_admin/v1/gestion/receta/{id}")
    suspend fun getRecetaById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): JsonObject

    // ================
    // ENFERMEDAD
    // ================
    @GET("ords/wellfit_admin/v1/gestion/enfermedad")
    suspend fun getEnfermedades(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>

    // ================
    // OBJETIVO
    // ================
    @GET("ords/wellfit_admin/v1/gestion/objetivo")
    suspend fun getObjetivos(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>

    // ================
    // MEDICO
    // ================
    @GET("ords/wellfit_admin/v1/gestion/medico")
    suspend fun getMedicos(
        @Header("Authorization") authHeader: String
    ): List<JsonObject>
}
