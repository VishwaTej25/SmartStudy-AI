package com.example.smartstudy.backend

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val mobile: String = "",
    val email: String = "",
    val xp: Long = 0,
    val streak: Long = 0,
    val premiumPlan: String = "",
    val premiumUntil: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserSettings(
    val darkMode: Boolean = true,
    val aiVoice: Boolean = true,
    val notifications: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

data class Course(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val emoji: String = "",
    val order: Long = 0
)

data class Enrollment(
    val courseId: String = "",
    val progress: Double = 0.0,
    val enrolledAt: Long = System.currentTimeMillis()
)

data class StudyPlan(
    val id: String = "",
    val subject: String = "",
    val time: String = "",
    val priority: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class ChatEntry(
    val id: String = "",
    val text: String = "",
    val userMessage: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class TestAttempt(
    val id: String = "",
    val testId: String = "",
    val courseId: String = "",
    val title: String = "",
    val score: Long = 0,
    val totalQuestions: Long = 0,
    val percentage: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class ProgressSummary(
    val coursesEnrolled: Int = 0,
    val testsAttempted: Long = 0,
    val averageScore: Long = 0,
    val learningStreak: Long = 0,
    val courseProgress: List<Pair<String, Double>> = emptyList()
)
