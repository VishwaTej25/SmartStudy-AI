package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.UserProfile

@Composable
fun PremiumScreen() {
    val backend = remember { BackendProvider.backend }
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var selectedPlan by remember { mutableStateOf("monthly") }
    var message by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = backend.listenProfile(
            onUpdate = { profile = it },
            onError = { message = it.localizedMessage }
        )

        onDispose {
            listener?.remove()
        }
    }

    val premiumActive = (profile?.premiumUntil ?: 0L) > System.currentTimeMillis()

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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Premium Plans",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text =
                    if (premiumActive)
                        "Current plan: ${profile?.premiumPlan.orEmpty()}"
                    else
                        "Unlock advanced AI learning features",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            PremiumCard(
                title = "Monthly Plan",
                price = "Rs 199 / month",
                features = "AI Mentor - Unlimited Tests - Analytics",
                color = Color(0xFF7C3AED),
                selected = selectedPlan == "monthly",
                onClick = { selectedPlan = "monthly" }
            )

            PremiumCard(
                title = "Yearly Plan",
                price = "Rs 1499 / year",
                features = "All Features + Priority AI Access",
                color = Color(0xFF2563EB),
                selected = selectedPlan == "yearly",
                onClick = { selectedPlan = "yearly" }
            )

            message?.let {
                Text(
                    text = it,
                    color = Color(0xFFFFA8A8)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    backend.activatePremium(selectedPlan) { result ->
                        result
                            .onSuccess { message = "Premium activated for demo checkout." }
                            .onFailure { message = it.localizedMessage }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED)
                )
            ) {
                Text(
                    text =
                        if (premiumActive)
                            "Extend Plan"
                        else
                            "Upgrade Now",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PremiumCard(
    title: String,
    price: String,
    features: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected)
                    Color(0xFF273449)
                else
                    Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = price,
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = features,
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
    }
}
