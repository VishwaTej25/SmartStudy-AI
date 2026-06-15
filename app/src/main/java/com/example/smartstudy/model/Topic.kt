package com.example.smartstudy.model

data class Topic(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val videoUrl: String = "",
    val completed: Boolean = false
)