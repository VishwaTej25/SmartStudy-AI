package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.GroqHelper
import kotlinx.coroutines.launch

@Composable
fun AiScreen() {
    val backend = remember { BackendProvider.backend }
    val scope = rememberCoroutineScope()

    var enrolledCourses by remember { mutableStateOf<List<String>>(emptyList()) }
    var aiInsights by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val backgroundEndColor = if (isDark) Color(0xFF001D5C) else Color(0xFFE5E7EB)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)

    // Fetch enrollments to personalise AI suggestions
    DisposableEffect(Unit) {
        val reg = backend.listenEnrollments(
            onUpdate = { enrollmentMap ->
                enrolledCourses = enrollmentMap.values.map { it.courseId }
            },
            onError = { enrolledCourses = emptyList() }
        )
        onDispose { reg?.remove() }
    }

    // Once we know which courses are enrolled, ask Groq for personalised insights
    LaunchedEffect(enrolledCourses) {
        isLoading = true
        val courseList = if (enrolledCourses.isNotEmpty())
            enrolledCourses.joinToString(", ")
        else
            "General Computer Science"

        scope.launch {
            try {
                val prompt = """
                    A student is studying: $courseList.
                    Give personalised AI study insights in JSON with these exact keys:
                    weak_area, suggested_topic, daily_goal, prediction, recommendation.
                    Values should be short (1 sentence each), specific to their enrolled courses.
                    Return ONLY valid JSON, no markdown, no explanation.
                """.trimIndent()

                val raw = GroqHelper.ask(prompt)
                val json = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

                val map = mutableMapOf<String, String>()
                Regex("\"(\\w+)\"\\s*:\\s*\"([^\"]+)\"").findAll(json).forEach { m ->
                    map[m.groupValues[1]] = m.groupValues[2]
                }
                if (map.isNotEmpty()) aiInsights = map
            } catch (_: Exception) {
                // silently use empty map - fallback labels shown below
            } finally {
                isLoading = false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(backgroundColor, backgroundEndColor))
            )
            .padding(16.dp)
    ) {
        item {
            Spacer(Modifier.height(10.dp))

            Text(
                text = "AI Study Assistant 🤖",
                color = textColorMain,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (enrolledCourses.isNotEmpty())
                    "Personalised insights for: ${enrolledCourses.take(3).joinToString(", ")}"
                else
                    "Personal AI insights for your learning",
                color = textColorMuted,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(25.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = Color(0xFF8B3DFF))
                        Text("Generating AI insights…", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            } else {
                AIBox(
                    "🎯 Weak Area",
                    aiInsights["weak_area"] ?: "Complete more topics to identify weak areas"
                )
                Spacer(Modifier.height(15.dp))
                AIBox(
                    "📚 Suggested Topic",
                    aiInsights["suggested_topic"] ?: "Enrol in a course to get topic suggestions"
                )
                Spacer(Modifier.height(15.dp))
                AIBox(
                    "🔥 Daily Goal",
                    aiInsights["daily_goal"] ?: "Study for at least 30 minutes today"
                )
                Spacer(Modifier.height(15.dp))
                AIBox(
                    "📈 Prediction",
                    aiInsights["prediction"] ?: "Keep studying to unlock performance predictions"
                )
                Spacer(Modifier.height(15.dp))
                AIBox(
                    "🧠 AI Recommendation",
                    aiInsights["recommendation"] ?: "Keep consistent — daily practice is key!"
                )
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun AIBox(
    title: String,
    value: String
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                color = textColorMain,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}