package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider

data class Question(
    val question:String,
    val options:List<String>,
    val answer:Int
)

@Composable
fun TestScreen(){
    val backend = remember { BackendProvider.backend }

    val questions = listOf(

        Question(
            "Java is a ?",
            listOf(
                "Programming Language",
                "Database",
                "Browser",
                "OS"
            ),
            0
        ),

        Question(
            "HTML stands for?",
            listOf(
                "Hyper Text Markup Language",
                "High Text Machine Language",
                "Hyper Transfer ML",
                "None"
            ),
            0
        ),

        Question(
            "Firebase is?",
            listOf(
                "Database Platform",
                "Game",
                "Compiler",
                "Browser"
            ),
            0
        )

    )

    var currentQuestion by remember {
        mutableStateOf(0)
    }

    var selectedAnswer by remember {
        mutableStateOf(-1)
    }

    var score by remember {
        mutableStateOf(0)
    }

    var showResult by remember {
        mutableStateOf(false)
    }

    var savedResult by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(showResult) {
        if (showResult && !savedResult) {
            savedResult = true
            backend.saveTestAttempt(
                testId = "mock-test",
                courseId = "general",
                title = "Mock Test",
                score = score,
                totalQuestions = questions.size
            ) { result ->
                result.onFailure { error = it.localizedMessage }
            }
        }
    }

    Box(

        modifier=
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF050B1A),
                            Color(0xFF0A1B55)
                        )
                    )
                )
                .padding(20.dp)

    ){

        if(showResult){

            Column(

                modifier=
                    Modifier.fillMaxSize(),

                horizontalAlignment=
                    Alignment.CenterHorizontally,

                verticalArrangement=
                    Arrangement.Center

            ){

                Text(
                    "Test Completed 🎉",
                    color=Color.White,
                    fontSize=30.sp,
                    fontWeight=FontWeight.Bold
                )

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    "Score : $score/${questions.size}",
                    color=Color(0xFF8B3DFF),
                    fontSize=24.sp
                )

                error?.let {
                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(
                        it,
                        color=Color(0xFFFFA8A8)
                    )
                }

            }

        }

        else{

            val question=
                questions[currentQuestion]

            Column {

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    "Mock Test 📝",
                    color=Color.White,
                    fontSize=30.sp,
                    fontWeight=FontWeight.Bold
                )

                Spacer(
                    Modifier.height(30.dp)
                )

                Card(

                    colors=
                        CardDefaults.cardColors(
                            containerColor=
                                Color(0xFF1A2033)
                        ),

                    shape=
                        RoundedCornerShape(18.dp)

                ){

                    Column(
                        modifier=
                            Modifier.padding(18.dp)
                    ){

                        Text(

                            "Q${currentQuestion+1}. ${question.question}",

                            color=Color.White,

                            fontSize=20.sp

                        )

                    }

                }

                Spacer(
                    Modifier.height(20.dp)
                )

                question.options.forEachIndexed{
                        index,option->

                    Card(

                        modifier=
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical=6.dp
                                ),

                        colors=
                            CardDefaults.cardColors(
                                containerColor=
                                    if(selectedAnswer==index)
                                        Color(0xFF8B3DFF)
                                    else
                                        Color(0xFF1A2033)
                            ),

                        onClick={

                            selectedAnswer=index

                        }

                    ){

                        Text(

                            option,

                            color=Color.White,

                            modifier=
                                Modifier.padding(18.dp)

                        )

                    }

                }

                Spacer(
                    Modifier.height(25.dp)
                )

                Button(

                    onClick={

                        if(
                            selectedAnswer==
                            question.answer
                        ){

                            score++

                        }

                        selectedAnswer=-1

                        if(
                            currentQuestion
                            <
                            questions.size-1
                        ){

                            currentQuestion++

                        }

                        else{

                            showResult=true

                        }

                    },

                    modifier=
                        Modifier.fillMaxWidth()

                ){

                    Text(
                        "Next"
                    )

                }

            }

        }

    }

}
