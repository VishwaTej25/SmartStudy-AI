package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications

import androidx.compose.material3.*

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationItem(
    val title: String,
    val subtitle: String
)

@Composable
fun NotificationScreen() {

    val notifications = listOf(

        NotificationItem(
            title = "📚 Study Reminder",
            subtitle = "Time to revise Java Programming"
        ),

        NotificationItem(
            title = "📝 Mock Test Alert",
            subtitle = "Complete your DBMS mock test today"
        ),

        NotificationItem(
            title = "🔥 Streak Reminder",
            subtitle = "You are on a 15-day streak"
        ),

        NotificationItem(
            title = "🤖 AI Suggestion",
            subtitle = "AI predicts low consistency in OS"
        )
    )

    val backgroundBrush = Brush.verticalGradient(

        colors = listOf(
            Color(0xFF0B1020),
            Color(0xFF121826),
            Color(0xFF1E1B4B)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(20.dp)
    ) {

        // TOP TITLE

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFF7C3AED)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Smart Notifications",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NOTIFICATION LIST

        LazyColumn(

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            items(notifications) { item ->

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(24.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = item.subtitle,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}