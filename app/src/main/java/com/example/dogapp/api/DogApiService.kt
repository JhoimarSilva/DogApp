package com.example.dogapp.api

import retrofit2.Response
import retrofit2.http.GET

interface DogApiService {
    @GET("breeds/list/all")
    suspend fun getAllBreeds(): Response<DogBreedResponse>
}

data class DogBreedResponse(
    val message: Map<String, List<String>>,
    val status: String
)