package com.example.smartstudy.model

data class Course(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val emoji: String = "📘",
    val progress: Int = 0,
    val enrolled: Boolean = false,
    val order: Int = 0
)