package com.example.smartstudy.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {

    var startAnimation by remember {
        mutableStateOf(false)
    }

    val scale =
        animateFloatAsState(
            targetValue =
                if (startAnimation) 1f else 0.5f,

            animationSpec = tween(
                durationMillis = 1200
            ),

            label = ""
        )

    val alpha =
        animateFloatAsState(
            targetValue =
                if (startAnimation) 1f else 0f,

            animationSpec = tween(
                durationMillis = 1500
            ),

            label = ""
        )

    LaunchedEffect(true) {

        startAnimation = true

        delay(2500)

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1020),
                        Color(0xFF121826),
                        Color(0xFF1E1B4B)
                    )
                )
            ),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "🧠",
                fontSize = 90.sp,

                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Smart Study AI",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,

                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AI-Powered Learning Platform 🚀",
                color = Color.Gray,
                fontSize = 16.sp,

                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Loading...",
                color = Color(0xFF7C3AED),
                fontSize = 18.sp,

                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}