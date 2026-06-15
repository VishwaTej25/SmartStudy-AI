package com.example.smartstudy.backend

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GroqHelper {

    private const val API_KEY = "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"

    private val api = Retrofit.Builder()
        .baseUrl("https://api.groq.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GroqService::class.java)

    suspend fun ask(question: String): String {

        return try {

            val response = api.chat(
                token = "Bearer $API_KEY",
                request = GroqRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(
                        GroqMessage(
                            role = "user",
                            content = question
                        )
                    )
                )
            )

            response.choices.firstOrNull()?.message?.content
                ?: "No response"

        } catch (e: Exception) {
            "ERROR: ${e.message}"
        }
    }
}