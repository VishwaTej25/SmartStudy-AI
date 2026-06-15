package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.GroqHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TestQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

@Composable
fun TopicTestScreen(
    courseName: String = "Java OOPs Concepts",
    courseId: String = "java",
    onBack: () -> Unit
) {
    val backend = remember { BackendProvider.backend }
    val scope = rememberCoroutineScope()

    // ── Load 50 MCQ questions from Groq ─────────────────────────────────────
    var questions by remember { mutableStateOf<List<TestQuestion>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var fetchError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(courseName) {
        val prompt = "Generate 50 multiple-choice questions for $courseName. " +
                "Each question must have exactly four options labeled A, B, C, D and indicate the correct answer letter. " +
                "Separate entries with a blank line. Use this exact format:\n" +
                "Question: <question>\nOptions: A) <opt1>, B) <opt2>, C) <opt3>, D) <opt4>\nAnswer: <letter>"
        try {
            val raw = GroqHelper.ask(prompt)
            val blocks = raw.split("\n\n")
            val parsed = mutableListOf<TestQuestion>()
            for (block in blocks) {
                val lines = block.lines().filter { it.isNotBlank() }
                if (lines.size >= 3) {
                    val q = lines[0].removePrefix("Question:").trim()
                    val optsPart = lines[1].removePrefix("Options:").trim()
                    val options = optsPart.split(Regex(",\\s*(?=[A-D]\\))"))
                        .map { it.substringAfter(")").trim() }
                    val ansLetter = lines[2].removePrefix("Answer:").trim().uppercase()
                    val correct = when (ansLetter) {
                        "A" -> options.getOrNull(0) ?: ""
                        "B" -> options.getOrNull(1) ?: ""
                        "C" -> options.getOrNull(2) ?: ""
                        "D" -> options.getOrNull(3) ?: ""
                        else -> options.getOrNull(0) ?: ""
                    }
                    if (q.isNotBlank() && options.size == 4) {
                        parsed.add(TestQuestion(question = q, options = options, correctAnswer = correct))
                    }
                }
            }
            questions = parsed
        } catch (e: Exception) {
            fetchError = e.localizedMessage
        } finally {
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF000814), Color(0xFF001D5C)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressIndicator(color = Color(0xFF8B3DFF))
                Text("Generating 50 AI questions for $courseName…", color = Color.LightGray, fontSize = 14.sp)
            }
        }
        return
    }

    if (fetchError != null || questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000814)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("⚠️ Failed to load questions", color = Color(0xFFFFA8A8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                fetchError?.let { Text(it, color = Color.LightGray, fontSize = 13.sp) }
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3DFF))) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    // ── Quiz state ───────────────────────────────────────────────────────────
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }
    var selectedAnswer by rememberSaveable { mutableStateOf("") }
    var score by rememberSaveable { mutableStateOf(0) }
    var testFinished by rememberSaveable { mutableStateOf(false) }
    // 30 minutes = 1800 seconds
    var timeLeft by rememberSaveable { mutableStateOf(1800) }
    var savedResult by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    // AI analysis state
    var aiAnalysis by rememberSaveable { mutableStateOf<String?>(null) }
    var aiLoading by rememberSaveable { mutableStateOf(false) }

    // ── Countdown timer ──────────────────────────────────────────────────────
    LaunchedEffect(timeLeft, testFinished) {
        if (timeLeft > 0 && !testFinished) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0 && !testFinished) {
            testFinished = true
        }
    }

    // ── Save result + generate AI analysis when test finishes ────────────────
    LaunchedEffect(testFinished) {
        if (testFinished && !savedResult) {
            savedResult = true
            backend.saveTestAttempt(
                testId = "${courseId}-assessment",
                courseId = courseId,
                title = "$courseName Assessment",
                score = score,
                totalQuestions = questions.size
            ) { result ->
                result.onFailure { error = it.localizedMessage }
            }
            // Generate real AI analysis
            aiLoading = true
            scope.launch {
                val pct = (score * 100 / questions.size)
                val prompt = "A student just completed a 50-question $courseName multiple-choice test " +
                        "and scored $score out of ${questions.size} ($pct%). " +
                        "Provide a detailed personalised AI analysis (around 150 words) covering: " +
                        "1) overall performance, 2) which areas they likely struggled with based on the score, " +
                        "3) specific study recommendations, and 4) an encouraging closing remark."
                aiAnalysis = try { GroqHelper.ask(prompt) } catch (e: Exception) { null }
                aiLoading = false
            }
        }
    }

    // ── Result Screen ────────────────────────────────────────────────────────
    if (testFinished) {
        val pct = if (questions.isNotEmpty()) (score * 100 / questions.size) else 0
        val grade = when {
            pct >= 90 -> "🏆 Outstanding"
            pct >= 75 -> "🥇 Excellent"
            pct >= 60 -> "🥈 Good"
            pct >= 40 -> "🥉 Average"
            else -> "📚 Needs Work"
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF000814), Color(0xFF001D5C))))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                Text("🎉 Test Completed!", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(grade, color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            item {
                // Score card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2235)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Your Score", color = Color.LightGray, fontSize = 14.sp)
                        Text(
                            "$score / ${questions.size}",
                            color = Color(0xFF00E5FF),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("$pct%", color = Color(0xFF8B3DFF), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = pct / 100f,
                            modifier = Modifier.fillMaxWidth().height(8.dp).then(
                                Modifier.padding(horizontal = 4.dp)
                            ),
                            color = when {
                                pct >= 75 -> Color(0xFF10B981)
                                pct >= 50 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            },
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            item {
                // AI analysis card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2027)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🤖", fontSize = 22.sp)
                            Text("AI Analysis", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        when {
                            aiLoading -> {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator(color = Color(0xFF8B3DFF), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Text("Generating personalised AI feedback…", color = Color.LightGray, fontSize = 13.sp)
                                }
                            }
                            aiAnalysis != null -> {
                                Text(aiAnalysis!!, color = Color(0xFFD1D5DB), fontSize = 14.sp, lineHeight = 22.sp)
                            }
                            else -> {
                                // Fallback local analysis if AI fails
                                val fallback = when {
                                    pct >= 75 -> "Excellent work! You have a strong grasp of $courseName. Focus on advanced topics to further enhance your skills."
                                    pct >= 50 -> "Good effort! Revise core concepts of $courseName. Practice more to consolidate your understanding."
                                    else -> "Keep practicing! Focus on foundational principles of $courseName. Consistent daily study will help improve your score significantly."
                                }
                                Text(fallback, color = Color(0xFFD1D5DB), fontSize = 14.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }

            error?.let {
                item { Text(it, color = Color(0xFFFFA8A8), fontSize = 12.sp) }
            }

            item {
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3DFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Back", fontSize = 16.sp)
                }
                Spacer(Modifier.height(40.dp))
            }
        }
        return
    }

    // ── Active Quiz ──────────────────────────────────────────────────────────
    val currentQuestion = questions[currentQuestionIndex]
    val timerColor = when {
        timeLeft > 600 -> Color(0xFF10B981)
        timeLeft > 300 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF000814), Color(0xFF001D5C))))
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "$courseName Assessment 📝",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Progress & Timer row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q ${currentQuestionIndex + 1} / ${questions.size}",
                color = Color(0xFF00E5FF),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "⏱ ${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",
                color = timerColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        // Overall progress bar
        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1).toFloat() / questions.size.toFloat(),
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Color(0xFF8B3DFF),
            trackColor = Color.White.copy(alpha = 0.1f)
        )

        Spacer(Modifier.height(20.dp))

        // Question card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2235)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = currentQuestion.question,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 25.sp
                )

                Spacer(Modifier.height(20.dp))

                currentQuestion.options.forEachIndexed { idx, option ->
                    val label = listOf("A", "B", "C", "D").getOrElse(idx) { "${idx + 1}" }
                    val isSelected = selectedAnswer == option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedAnswer = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF8B3DFF),
                                unselectedColor = Color(0xFF6B7280)
                            )
                        )
                        Text(
                            text = "$label. $option",
                            color = if (isSelected) Color(0xFF8B3DFF) else Color.White,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Next / Finish button
        Button(
            onClick = {
                if (selectedAnswer == currentQuestion.correctAnswer) score++
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    selectedAnswer = ""
                } else {
                    testFinished = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3DFF)),
            shape = RoundedCornerShape(14.dp),
            enabled = selectedAnswer.isNotBlank()
        ) {
            Text(
                text = if (currentQuestionIndex == questions.size - 1) "Finish Test 🏁" else "Next Question →",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
