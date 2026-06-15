package com.example.smartstudy.backend

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqService {

    @POST("openai/v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body request: GroqRequest
    ): GroqResponse
}