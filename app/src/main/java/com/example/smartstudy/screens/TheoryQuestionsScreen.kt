package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.GroqHelper

@Composable
fun TheoryQuestionsScreen(
    courseName: String = "Java OOPs",
    onBack: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var questions by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    LaunchedEffect(courseName) {
        loading = true
        error = null
        try {
            val prompt = "Generate 20 important theory-style questions with detailed answers for the university topic \"$courseName\". " +
                    "Format each as:\nQuestion: <question>\nAnswer: <answer>\n\nSeparate entries with a blank line."
            val raw = GroqHelper.ask(prompt)
            val list = raw.split("\n\n").mapNotNull { block ->
                val qIdx = block.indexOf("Question:")
                val aIdx = block.indexOf("Answer:")
                if (qIdx != -1 && aIdx != -1) {
                    val q = block.substring(qIdx + 9, aIdx).trim()
                    val a = block.substring(aIdx + 7).trim()
                    if (q.isNotBlank() && a.isNotBlank()) q to a else null
                } else null
            }
            questions = list
        } catch (e: Exception) {
            error = e.localizedMessage
        } finally {
            loading = false
        }
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val textColor = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBg = if (isDark) Color(0xFF1B2235) else Color.White
    val answerBg = if (isDark) Color(0xFF0D1830) else Color(0xFFEEF2FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Column {
                Text(
                    text = "Theory Q&A",
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = courseName,
                    color = Color(0xFF8B3DFF),
                    fontSize = 13.sp
                )
            }
        }

        when {
            loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = Color(0xFF8B3DFF))
                        Text("Generating AI theory questions…", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("⚠️ Failed to load questions", color = Color(0xFFFFA8A8), fontSize = 16.sp)
                        Text(error ?: "", color = Color.LightGray, fontSize = 12.sp)
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3DFF))) {
                            Text("Go Back")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(questions.withIndex().toList()) { (index, qa) ->
                        ExpandableQACard(
                            number = index + 1,
                            question = qa.first,
                            answer = qa.second,
                            textColor = textColor,
                            cardBg = cardBg,
                            answerBg = answerBg
                        )
                    }
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }
}

@Composable
fun ExpandableQACard(
    number: Int,
    question: String,
    answer: String,
    textColor: Color,
    cardBg: Color,
    answerBg: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF8B3DFF).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$number", color = Color(0xFF8B3DFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = question,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = Color(0xFF8B3DFF),
                    fontSize = 12.sp
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(answerBg, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = answer,
                        color = textColor.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
