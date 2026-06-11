package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PracticeCourse(
    val title:String,
    val subtitle:String,
    val emoji:String
)

@Composable
fun PracticeScreen() {

    // Temporary enrolled courses

    val enrolledCourses = listOf(

        PracticeCourse(
            "Java Programming",
            "Practice Java coding & MCQs",
            "☕"
        ),

        PracticeCourse(
            "DBMS",
            "SQL queries & database MCQs",
            "🗄"
        ),

        PracticeCourse(
            "DSA",
            "Coding problems & logic building",
            "💻"
        )

    )

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

                text = "Practice Zone 🎯",

                color = Color.White,

                fontSize = 30.sp,

                fontWeight = FontWeight.Bold

            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text =
                    "Practice your enrolled courses",

                color = Color.LightGray

            )

            Spacer(
                Modifier.height(25.dp)
            )

        }

        items(enrolledCourses){ course ->

            PracticeCourseCard(course)

            Spacer(
                Modifier.height(15.dp)
            )

        }

    }

}

@Composable
fun PracticeCourseCard(
    course: PracticeCourse
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

                text=course.emoji,

                fontSize=40.sp

            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(

                text=course.title,

                color=Color.White,

                fontSize=22.sp,

                fontWeight=FontWeight.Bold

            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text=course.subtitle,

                color=Color.LightGray

            )

            Spacer(
                Modifier.height(15.dp)
            )

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)

            ){

                Button(

                    onClick = { },

                    colors=
                        ButtonDefaults.buttonColors(
                            containerColor=
                                Color(0xFF8B3DFF)
                        )

                ) {

                    Text("Coding")

                }

                Button(

                    onClick = { },

                    colors=
                        ButtonDefaults.buttonColors(
                            containerColor=
                                Color(0xFF00B894)
                        )

                ) {

                    Text("MCQs")

                }

            }

        }

    }

}