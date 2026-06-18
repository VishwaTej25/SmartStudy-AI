package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.Course
import com.example.smartstudy.backend.Enrollment
import com.example.smartstudy.backend.GroqHelper
import com.example.smartstudy.model.Topic
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun CourseDetailsScreen(
    course: Course = Course(
        id = "java",
        title = "Java Programming",
        subtitle = "Core Java + OOPs + Advanced Java",
        emoji = "☕"
    ),
    onBack: () -> Unit = {}
) {
    val backend = remember { BackendProvider.backend }

    var openLearnScreen by remember { mutableStateOf(false) }
    var openTestScreen by remember { mutableStateOf(false) }
    var selectedTopic by remember { mutableStateOf<Topic?>(null) }
    var topics by remember { mutableStateOf<List<Topic>>(emptyList()) }
    var topicsLoading by remember { mutableStateOf(true) }
    var enrollment by remember { mutableStateOf<Enrollment?>(null) }

    // Load AI-generated topics for this course
    LaunchedEffect(course.id) {
        topicsLoading = true
        try {
            val prompt = """
                Generate exactly 7 key study topics for the university course "${course.title}".
                For each topic provide a 1-sentence description.
                Return ONLY a JSON array (no markdown), each object having "title" and "content" fields.
            """.trimIndent()
            val raw = GroqHelper.ask(prompt)
            var json = raw.trim()
            if (json.startsWith("```")) {
                json = json.lines().filter { !it.trim().startsWith("```") }.joinToString("\n").trim()
            }
            val type = object : TypeToken<List<Topic>>() {}.type
            topics = Gson().fromJson(json, type)
        } catch (e: Exception) {
            topics = com.example.smartstudy.model.TopicRepository.getTopicsForCourse(course.id, course.title)
        } finally {
            topicsLoading = false
        }
    }

    // Listen to enrollment progress
    DisposableEffect(course.id) {
        val listener = backend.listenEnrollments(
            onUpdate = { map -> enrollment = map[course.id] },
            onError = {}
        )
        onDispose { listener?.remove() }
    }

    // Sub-screen routing
    if (openLearnScreen && selectedTopic != null) {
        TopicLearnScreen(
            courseName = course.title,
            courseId = course.id,
            onBack = { openLearnScreen = false; selectedTopic = null }
        )
        return
    }
    if (openTestScreen) {
        TopicTestScreen(
            courseName = course.title,
            courseId = course.id,
            onBack = { openTestScreen = false }
        )
        return
    }

    // Accent gradient based on course
    val accentStart = Color(0xFF7C3AED)
    val accentEnd   = Color(0xFF4F46E5)
    val progress    = enrollment?.progress?.toFloat() ?: 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A14)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // ── Gradient header ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(accentStart, accentEnd)))
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 50.dp, bottom = 8.dp)
                ) {
                    // Back button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .clickableNoRipple { onBack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", color = Color.White, fontSize = 15.sp)
                    }

                    // Emoji icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(course.emoji, fontSize = 30.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = course.title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = course.subtitle,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${(progress * topics.size).toInt()}/${topics.size} topics done",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            // ── Quick actions row ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    emoji = "📝",
                    label = "Take Test",
                    containerColor = Color(0xFF1E1E4A),
                    textColor = Color(0xFF818CF8)
                ) { openTestScreen = true }

                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    emoji = "🤖",
                    label = "AI Chat",
                    containerColor = Color(0xFF0D2820),
                    textColor = Color(0xFF34D399)
                ) { openLearnScreen = true; selectedTopic = topics.firstOrNull() }

                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    emoji = "📄",
                    label = "PDF Notes",
                    containerColor = Color(0xFF1A2035),
                    textColor = Color(0xFF60A5FA)
                ) { openLearnScreen = true; selectedTopic = topics.firstOrNull() }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "📚 Topics",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (topicsLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(color = Color(0xFF8B3DFF))
                        Text("Loading topics for ${course.title}…", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
        } else {
            topics.forEachIndexed { index, topic ->
                item {
                    TopicCard(
                        number = index + 1,
                        emoji = topicEmojis.getOrElse(index) { "📖" },
                        title = topic.title,
                        subtitle = topic.content,
                        onLearnClick = {
                            selectedTopic = topic
                            openLearnScreen = true
                        },
                        onTestClick = { openTestScreen = true }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

private val topicEmojis = listOf("📘","📗","📕","📙","📒","📓","📔","📃","📜","📄")

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    containerColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 16.sp)
            Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// Extension to remove ripple on back button text
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    ) { onClick() }
}

@Composable
fun TopicCard(
    number: Int,
    emoji: String,
    title: String,
    subtitle: String,
    onLearnClick: () -> Unit,
    onTestClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14142A)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1E3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$number", color = Color(0xFF9CA3AF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Text(emoji, fontSize = 20.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, color = Color(0xFF6B7280), fontSize = 12.sp, maxLines = 2)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Learn
                Button(
                    onClick = onLearnClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E4A))
                ) {
                    Text("📖 ", fontSize = 14.sp)
                    Text("Learn", color = Color(0xFF818CF8), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                // Practice
                Button(
                    onClick = onLearnClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2035))
                ) {
                    Text("💻 ", fontSize = 14.sp)
                    Text("Practice", color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                // Assess
                Button(
                    onClick = onTestClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D2820))
                ) {
                    Text("📝 ", fontSize = 14.sp)
                    Text("Assess", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}