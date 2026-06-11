package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalyticsChart() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "📊 AI Weekly Analytics",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceEvenly,

                verticalAlignment =
                    Alignment.Bottom
            ) {

                ChartBar(70.dp, "Mon")
                ChartBar(110.dp, "Tue")
                ChartBar(90.dp, "Wed")
                ChartBar(150.dp, "Thu")
                ChartBar(120.dp, "Fri")
                ChartBar(160.dp, "Sat")
            }
        }
    }
}

@Composable
fun ChartBar(
    height: androidx.compose.ui.unit.Dp,
    label: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .width(20.dp)
                .height(height)
                .background(
                    Color(0xFF7C3AED),
                    RoundedCornerShape(20.dp)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}