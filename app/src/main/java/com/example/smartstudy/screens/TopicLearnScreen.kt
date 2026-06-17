package com.example.smartstudy.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.example.smartstudy.backend.GroqHelper


@Composable
fun TopicLearnScreen(
    courseName: String,
    onBack: () -> Unit
) {
    val scrollState = rememberLazyListState()

    var selectedFeature by remember {
        mutableStateOf("")
    }
    when (selectedFeature) {

        "questions" -> {
            TheoryQuestionsScreen(
                courseName = courseName,
                onBack = { selectedFeature = "" }
            )
            return
        }

        "quiz" -> {
            // Dynamically fetch MCQ questions from Groq
            var questions by remember { mutableStateOf<List<TestQuestion>>(emptyList()) }
            var loading by remember { mutableStateOf(true) }
            var fetchError by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(courseName) {
                val prompt = "Generate 30 multiple‑choice questions for $courseName concepts. " +
                        "Each question should have exactly four options labeled A, B, C, D and indicate the correct answer letter. " +
                        "Return the list in the following plain‑text format separated by blank lines: " +
                        "Question: <question>\nOptions: A) <opt1>, B) <opt2>, C) <opt3>, D) <opt4>\nAnswer: <letter>"
                try {
                    val raw = GroqHelper.ask(prompt)
                    // Simple parser – split blocks by double newline
                    val blocks = raw.split("\n\n")
                    val parsed = mutableListOf<TestQuestion>()
                    for (block in blocks) {
                        val lines = block.lines().filter { it.isNotBlank() }
                        if (lines.size >= 3) {
                            val q = lines[0].removePrefix("Question: ").trim()
                            val optsPart = lines[1].removePrefix("Options: ").trim()
                            val options = optsPart.split(",").map { it.substringAfter(")").trim() }
                            val ansLetter = lines[2].removePrefix("Answer: ").trim()
                            // Map letter to option string
                            val correct = when (ansLetter.uppercase()) {
                                "A" -> options.getOrNull(0) ?: ""
                                "B" -> options.getOrNull(1) ?: ""
                                "C" -> options.getOrNull(2) ?: ""
                                "D" -> options.getOrNull(3) ?: ""
                                else -> ""
                            }
                            parsed.add(TestQuestion(question = q, options = options, correctAnswer = correct))
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
                // Simple loading UI
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8B3DFF))
                }
                return
            }
            if (fetchError != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = fetchError!!, color = Color(0xFFFFA8A8))
                }
                return
            }

            return
        }

        "video" -> {
            VideoScreen(
                courseName = courseName,
                onBack = { selectedFeature = "" }
            )
            return
        }

        "pdf" -> {
            PdfScreen(
                courseName = courseName,
                onBack = { selectedFeature = "" }
            )
            return
        }

        "ai" -> {
            AiExplanationScreen(
                courseName = courseName,
                onBack = {
                    selectedFeature = ""
                }
            )
            return
        }
    }


    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val backgroundEndColor = if (isDark) Color(0xFF001D5C) else Color(0xFFE5E7EB)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)

    LazyColumn(
        state = scrollState,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            backgroundColor,
                            backgroundEndColor
                        )
                    )
                )
                .padding(16.dp)

    ) {
        item {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = textColorMain
                )
            }
        }

        item {

            Text(
                text = "$courseName OOPs Concepts 📖",
                color = textColorMain,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Master $courseName with AI-powered study materials covering OOPs Concepts.",
                color = textColorMuted
            )

            Spacer(
                Modifier.height(25.dp)
            )

            Spacer(Modifier.height(20.dp))

            LearnCard(
                title = "🎥 Video Lectures",
                subtitle = "Watch Video Lectures and tutorials for $courseName",
                onClick = { selectedFeature = "video" }
            )

            Spacer(Modifier.height(12.dp))

            LearnCard(
                title = "📄 PDF Study Notes",
                subtitle = "Download AI-generated notes for $courseName",
                onClick = { selectedFeature = "pdf" }
            )

            Spacer(Modifier.height(12.dp))

            LearnCard(
                title = "🧠 AI Explanation",
                subtitle = "AI-powered explanations for $courseName",
                onClick = { selectedFeature = "ai" }
            )

            Spacer(Modifier.height(12.dp))

            LearnCard(
                title = "💡 Important Theory Q&A",
                subtitle = "AI-generated theory questions for $courseName",
                onClick = { selectedFeature = "questions" }
            )

            Spacer(Modifier.height(12.dp))

            LearnCard(
                title = "📝 MCQ Practice Test",
                subtitle = "50-question timed quiz for $courseName",
                onClick = { selectedFeature = "quiz" }
            )

        }

    }

}
@Composable
fun VideoScreen(
    courseName: String = "the Course",
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0A0A14) else Color(0xFFF3F4F6)
    val cardBg = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    // Video topics with YouTube search queries
    val videos = listOf(
        Triple("Introduction & Overview", "Foundational concepts of $courseName", "$courseName introduction tutorial"),
        Triple("Core Principles Explained", "Deep dive into $courseName key principles", "$courseName core concepts explained"),
        Triple("OOPs Concepts & Theory", "OOP design patterns and concepts for $courseName", "$courseName OOPs concepts tutorial"),
        Triple("Data Structures in $courseName", "Common data structures and algorithms applied", "$courseName data structures tutorial"),
        Triple("Practical Examples", "Coding walkthroughs and real-world scenarios", "$courseName practical coding examples"),
        Triple("Advanced Topics", "In-depth advanced $courseName techniques", "$courseName advanced topics tutorial"),
        Triple("Interview Preparation", "Top interview questions and answers for $courseName", "$courseName interview questions")
    )

    val accentColor = Color(0xFF8B3DFF)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 50.dp, end = 20.dp, bottom = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColorMain
                    )
                }
                Text(
                    text = "🎥 Video Lectures",
                    color = textColorMain,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap a video to watch on YouTube",
                color = textColorMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        items(videos) { (title, desc, searchQuery) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        val encoded = java.net.URLEncoder.encode(searchQuery, "UTF-8")
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://www.youtube.com/results?search_query=$encoded")
                        )
                        context.startActivity(intent)
                    },
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", fontSize = 24.sp, color = accentColor)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = textColorMain, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(3.dp))
                        Text(desc, color = textColorMuted, fontSize = 12.sp, maxLines = 2)
                    }
                    Text(
                        text = "▶ YouTube",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PdfScreen(
    courseName: String = "the Course",
    onBack: () -> Unit
) {

    var notes by remember {
        mutableStateOf("Loading AI Study Notes...")
    }

    LaunchedEffect(courseName) {
        notes = GroqHelper.ask(
            "Generate detailed PDF-style study notes covering OOPs Concepts, important topics, definitions, " +
            "examples, and exam tips for the course: $courseName. Use clear sections and bullet points."
        )
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0A0A14) else Color(0xFFF3F4F6)
    val cardBg = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColorMain
                    )
                }
                Text(
                    text = "📄 Study Notes",
                    color = textColorMain,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "AI-generated notes for $courseName",
                color = textColorMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (notes == "Loading AI Study Notes...") {
                        CircularProgressIndicator(color = Color(0xFF8B3DFF), modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Generating AI study notes...", color = textColorMuted, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Text(
                            text = notes,
                            color = textColorMain,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun AiExplanationScreen(
    courseName: String,
    onBack: () -> Unit
) {

    var explanation by remember {
        mutableStateOf("Loading AI Explanation...")
    }

    LaunchedEffect(Unit) {
        explanation = GroqHelper.ask(
            question = "Generate complete study material with important topics, explanations and examples for $courseName"
        )
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

        item {

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = explanation,
                color = textColorMain
            )
        }
    }
}

@Composable
fun LearnCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)

    Card(
        onClick = onClick,
        modifier=
            Modifier.fillMaxWidth(),

        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColor
            ),

        shape=
            RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    ){

        Column(

            modifier=
                Modifier.padding(20.dp)

        ){

            Text(

                text=title,

                color=textColorMain,

                fontSize=22.sp,

                fontWeight=FontWeight.Bold

            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text=subtitle,

                color=textColorMuted

            )

        }

    }

}