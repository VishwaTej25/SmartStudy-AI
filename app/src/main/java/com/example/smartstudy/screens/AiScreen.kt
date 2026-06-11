package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AiScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF000814),
                        Color(0xFF001D5C)
                    )
                )
            )
            .padding(16.dp)
    ) {

        item {

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text = "AI Study Assistant 🤖",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = "Personal AI insights for your learning",
                color = Color.LightGray
            )

            Spacer(
                Modifier.height(25.dp)
            )

            AIBox(
                "🎯 Weak Area",
                "Java Collections"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            AIBox(
                "📚 Suggested Topic",
                "Practice DSA Arrays"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            AIBox(
                "🔥 Daily Goal",
                "Solve 5 Coding Problems"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            AIBox(
                "📈 Prediction",
                "91% chance of scoring A Grade"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            AIBox(
                "🧠 AI Recommendation",
                "Practice Java + Aptitude today"
            )

            Spacer(
                Modifier.height(100.dp)
            )
        }
    }
}

@Composable
fun AIBox(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B2235)
        ),

        shape = RoundedCornerShape(20.dp)

    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                color = Color.Gray
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

        }

    }
}