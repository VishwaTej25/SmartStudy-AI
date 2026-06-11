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
import kotlinx.coroutines.delay

data class TestQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

@Composable
fun TopicTestScreen() {
    val backend = remember { BackendProvider.backend }

    val questions = listOf(

        TestQuestion(
            "Which keyword is used to create an object blueprint in Java?",
            listOf("Function", "Class", "Object", "Method"),
            "Class"
        ),

        TestQuestion(
            "Which collection allows duplicate values?",
            listOf("Set", "HashSet", "ArrayList", "TreeSet"),
            "ArrayList"
        ),

        TestQuestion(
            "Which block always executes in exception handling?",
            listOf("catch", "throw", "finally", "try"),
            "finally"
        )

    )

    var currentQuestionIndex by remember {
        mutableStateOf(0)
    }

    var selectedAnswer by remember {
        mutableStateOf("")
    }

    var score by remember {
        mutableStateOf(0)
    }

    var testFinished by remember {
        mutableStateOf(false)
    }

    var timeLeft by remember {
        mutableStateOf(900)
    }

    var savedResult by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(timeLeft, testFinished) {

        if (timeLeft > 0 && !testFinished) {

            delay(1000)
            timeLeft--

        } else if (timeLeft == 0) {

            testFinished = true

        }

    }

    LaunchedEffect(testFinished) {
        if (testFinished && !savedResult) {
            savedResult = true
            backend.saveTestAttempt(
                testId = "java-assessment",
                courseId = "java",
                title = "Java Assessment",
                score = score,
                totalQuestions = questions.size
            ) { result ->
                result.onFailure { error = it.localizedMessage }
            }
        }
    }

    if (testFinished) {

        Column(
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
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Text(
                text = "🎉 Test Completed",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Score: $score / ${questions.size}",
                color = Color(0xFF00E5FF),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            error?.let {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = it,
                    color = Color(0xFFFFA8A8)
                )
            }

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2235)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "🤖 AI Analysis",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text =
                            if (score == questions.size)
                                "Excellent! You mastered this topic."
                            else if (score >= 2)
                                "Good job! Revise weak concepts once."
                            else
                                "Practice more questions and revise the topic.",

                        color = Color.LightGray
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Button(

                        onClick = {

                            // Progress screen later connect chestham

                        },

                        modifier = Modifier.fillMaxWidth(),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B3DFF)
                        )

                    ) {

                        Text("View Progress 📊")

                    }
                }

            }

        }

    } else {

        val currentQuestion = questions[currentQuestionIndex]

        Column(
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

            Text(
                text = "Java Assessment 📝",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Question ${currentQuestionIndex + 1}/${questions.size}",
                color = Color(0xFF00E5FF),
                fontSize = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    "⏱ Time Left: ${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",

                color = Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2235)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = currentQuestion.question,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    currentQuestion.options.forEach { option ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected = selectedAnswer == option,
                                onClick = {
                                    selectedAnswer = option
                                }
                            )

                            Text(
                                text = option,
                                color = Color.White
                            )

                        }

                    }

                }

            }

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {

                    if (selectedAnswer == currentQuestion.correctAnswer) {
                        score++
                    }

                    if (currentQuestionIndex < questions.size - 1) {

                        currentQuestionIndex++
                        selectedAnswer = ""

                    } else {

                        testFinished = true

                    }

                },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B3DFF)
                )

            ) {

                Text(
                    if (currentQuestionIndex == questions.size - 1)
                        "Finish Test"
                    else
                        "Next Question"
                )

            }

        }

    }

}
