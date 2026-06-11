package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CourseDetailsScreen() {

    var openLearnScreen by remember {

        mutableStateOf(false)

    }

    var openTestScreen by remember {

        mutableStateOf(false)

    }

    // OPEN LEARN SCREEN

    if(openLearnScreen){

        TopicLearnScreen()

    }

    // OPEN TEST SCREEN

    else if(openTestScreen){

        TopicTestScreen()

    }

    else {

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

                    text = "Java Programming ☕",

                    color = Color.White,

                    fontSize = 32.sp,

                    fontWeight = FontWeight.Bold

                )

                Spacer(
                    Modifier.height(10.dp)
                )

                Text(

                    text =
                        "Master Java from basics to advanced concepts.",

                    color = Color.LightGray

                )

                Spacer(
                    Modifier.height(25.dp)
                )

                TopicCard(
                    "📘 OOPs",
                    "Classes, Objects, Inheritance",

                    onLearnClick = {

                        openLearnScreen = true

                    },

                    onTestClick = {

                        openTestScreen = true

                    }

                )

                Spacer(
                    Modifier.height(15.dp)
                )

                TopicCard(
                    "📚 Collections",
                    "ArrayList, HashMap, Set",

                    onLearnClick = {

                        openLearnScreen = true

                    },

                    onTestClick = {

                        openTestScreen = true

                    }

                )

                Spacer(
                    Modifier.height(15.dp)
                )

                TopicCard(
                    "⚠ Exception Handling",
                    "try catch finally throw",

                    onLearnClick = {

                        openLearnScreen = true

                    },

                    onTestClick = {

                        openTestScreen = true

                    }

                )

                Spacer(
                    Modifier.height(15.dp)
                )

                TopicCard(
                    "🧵 Multithreading",
                    "Threads and concurrency",

                    onLearnClick = {

                        openLearnScreen = true

                    },

                    onTestClick = {

                        openTestScreen = true

                    }

                )

            }

        }

    }

}

@Composable
fun TopicCard(
    title:String,
    subtitle:String,
    onLearnClick: () -> Unit,
    onTestClick: () -> Unit
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

            Spacer(
                Modifier.height(15.dp)
            )

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)

            ){

                Button(

                    onClick = {

                        onLearnClick()

                    },

                    colors=
                        ButtonDefaults.buttonColors(
                            containerColor=
                                Color(0xFF8B3DFF)
                        )

                ) {

                    Text("Learn")

                }

                Button(

                    onClick = {

                        onTestClick()

                    },

                    colors=
                        ButtonDefaults.buttonColors(
                            containerColor=
                                Color(0xFF00B894)
                        )

                ) {

                    Text("Test")

                }

            }

        }

    }

}