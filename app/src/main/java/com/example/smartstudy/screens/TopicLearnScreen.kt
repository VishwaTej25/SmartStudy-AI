package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopicLearnScreen() {

    LazyColumn(

        modifier =
            Modifier
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

            Text(

                text = "OOPs Concepts 📘",

                color = Color.White,

                fontSize = 30.sp,

                fontWeight = FontWeight.Bold

            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(

                text =
                    "Learn Object Oriented Programming concepts in Java.",

                color = Color.LightGray

            )

            Spacer(
                Modifier.height(25.dp)
            )

            LearnCard(
                "📹 Video Lectures",
                "12 Java OOPs videos available"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            LearnCard(
                "📄 PDF Materials",
                "Download topic notes & PDFs"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            LearnCard(
                "🧠 AI Explanation",
                "AI generated smart explanations"
            )

            Spacer(
                Modifier.height(15.dp)
            )

            LearnCard(
                "💡 Important Questions",
                "Most asked interview questions"
            )

        }

    }

}

@Composable
fun LearnCard(
    title:String,
    subtitle:String
){

    Card(

        modifier=
            Modifier.fillMaxWidth(),

        colors=
            CardDefaults.cardColors(
                containerColor=
                    Color(0xFF1B2235)
            ),

        shape=
            RoundedCornerShape(20.dp)

    ){

        Column(

            modifier=
                Modifier.padding(20.dp)

        ){

            Text(

                text=title,

                color=Color.White,

                fontSize=22.sp,

                fontWeight=FontWeight.Bold

            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text=subtitle,

                color=Color.LightGray

            )

        }

    }

}