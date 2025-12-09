package com.example.wellfit.data.remote

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OracleGestionApiService {

    // GET /v1/gestion/desafio
    @GET("v1/gestion/desafio")
    suspend fun getDesafios(
        @Header("Authorization") bearerToken: String
    ): Response<JsonArray>

    // GET /v1/gestion/desafio/:id
    @GET("v1/gestion/desafio/{id}")
    suspend fun getDesafioById(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int
    ): Response<JsonObject>

    // GET /v1/gestion/ejercicio
    @GET("v1/gestion/ejercicio")
    suspend fun getEjercicios(
        @Header("Authorization") bearerToken: String
    ): Response<JsonArray>

    // GET /v1/gestion/ejercicio/:id
    @GET("v1/gestion/ejercicio/{id}")
    suspend fun getEjercicioById(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int
    ): Response<JsonObject>

    // GET /v1/gestion/receta
    @GET("v1/gestion/receta")
    suspend fun getRecetas(
        @Header("Authorization") bearerToken: String
    ): Response<JsonArray>

    // GET /v1/gestion/receta/:id
    @GET("v1/gestion/receta/{id}")
    suspend fun getRecetaById(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int
    ): Response<JsonObject>
}
