package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme

import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.Course
import com.example.smartstudy.backend.GroqHelper
import com.example.smartstudy.model.Topic
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment

@Composable
fun CourseTopicsScreen(
    course: Course,
    onBack: () -> Unit
) {

    val backend = BackendProvider.backend

    var topics by remember {
        mutableStateOf<List<Topic>>(emptyList())
    }

    var selectedTopic by remember {
        mutableStateOf<Topic?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    if (selectedTopic != null) {
        TopicLearnScreen(
            courseName = course.title,
            topicName = selectedTopic!!.title,
            onBack = {
                selectedTopic = null
            }
        )
        return
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0A0A14) else Color(0xFFF3F4F6)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)
    val cardBg = if (isDark) Color(0xFF1E1E2E) else Color(0xFFFFFFFF)

    LaunchedEffect(course.title) {
        isLoading = true
        errorMessage = null
        try {
            val prompt = """
                Generate a list of 5 key study topics for the course "${course.title}".
                For each topic, provide a title and a brief 1-2 sentence description of what the student will learn.
                Format your response strictly as a JSON array of objects, where each object has "title" and "content" fields. Do not include markdown code block syntax (like ```json) or any explanations. Output raw JSON only.
            """.trimIndent()

            var response = GroqHelper.ask(prompt)
            if (response.startsWith("ERROR:") || response.trim().isEmpty()) {
                throw Exception("Groq failed to generate topics: ${response}")
            }

            var json = response.trim()
            if (json.startsWith("```")) {
                val lines = json.split("\n")
                val cleanLines = lines.filter { !it.trim().startsWith("```") }
                json = cleanLines.joinToString("\n").trim()
            }

            val type = object : com.google.gson.reflect.TypeToken<List<Topic>>() {}.type
            val parsed: List<Topic> = com.google.gson.Gson().fromJson(json, type)
            topics = parsed
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Failed to generate topics: ${e.localizedMessage}"
            // Fallback default topics in case AI fails
            topics = com.example.smartstudy.model.TopicRepository.getTopicsForCourse(course.id, course.title)
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF8B3DFF))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColorMain
                    )
                }
            }
        }

        item {
            Text(
                text = course.title,
                fontSize = 24.sp,
                color = textColorMain,
                fontWeight = FontWeight.Bold
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color(0xFFFFA8A8),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(topics) { topic ->

            Card(
                onClick = {
                    selectedTopic = topic
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = topic.title,
                        fontSize = 18.sp,
                        color = textColorMain,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = topic.content,
                        color = textColorMuted
                    )
                }
            }
        }
    }
}