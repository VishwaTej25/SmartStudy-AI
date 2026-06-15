package com.example.smartstudy.backend

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String
)

data class GroqChoice(
    val message: GroqMessage
)

data class GroqResponse(
    val choices: List<GroqChoice>
)