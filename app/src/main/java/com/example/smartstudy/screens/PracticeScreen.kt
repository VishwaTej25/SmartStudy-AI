package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.smartstudy.backend.GeminiHelper
import com.example.smartstudy.backend.GroqHelper
import kotlinx.coroutines.launch


data class PracticeCourse(
    val title:String,
    val subtitle:String,
    val emoji:String
)

@Composable
fun PracticeScreen() {

    // Temporary enrolled courses


    var selectedPractice by remember {
        mutableStateOf("")
    }

    when(selectedPractice) {

        "java_mcq" -> {
            TopicTestScreen(
                onBack = {
                    selectedPractice = ""
                }
            )
            return
        }

        "java_code" -> {
            CodingPracticeScreen(
                onBack = {
                    selectedPractice = ""
                }
            )
            return
        }
    }

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

        items(enrolledCourses) { course ->

            PracticeCourseCard(
                course = course,

                onCodingClick = {
                    selectedPractice = "java_code"
                },

                onMcqClick = {
                    selectedPractice = "java_mcq"
                }
            )

            Spacer(
                Modifier.height(15.dp)
            )
        }

    }

}

data class CodingProblem(
    val title: String,
    val description: String,
    val starterCode: String
)

val codingProblems = listOf(
    CodingProblem(
        title = "Reverse a String",
        description = "Write a function reverseString(str: String): String that reverses the input string.\n\nExample:\nInput: \"hello\"\nOutput: \"olleh\"",
        starterCode = "fun reverseString(str: String): String {\n    // Write your code here\n    return \"\"\n}"
    ),
    CodingProblem(
        title = "Two Sum",
        description = "Write a function twoSum(nums: IntArray, target: Int): IntArray that returns indices of the two numbers such that they add up to the target.\n\nExample:\nInput: nums = [2, 7, 11, 15], target = 9\nOutput: [0, 1]",
        starterCode = "fun twoSum(nums: IntArray, target: Int): IntArray {\n    // Write your code here\n    return intArrayOf()\n}"
    ),
    CodingProblem(
        title = "Palindrome Number",
        description = "Write a function isPalindrome(n: Int): Boolean that checks if an integer n is a palindrome (reads the same backward as forward).\n\nExample:\nInput: 121 -> Output: true\nInput: -121 -> Output: false",
        starterCode = "fun isPalindrome(n: Int): Boolean {\n    // Write your code here\n    return false\n}"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingPracticeScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(0) }
    val problem = codingProblems[selectedIndex]

    var userCode by remember(selectedIndex) { mutableStateOf(problem.starterCode) }
    var consoleOutput by remember { mutableStateOf("Press 'Run Code & Evaluate' to test your solution.") }
    var isRunning by remember { mutableStateOf(false) }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val backgroundEndColor = if (isDark) Color(0xFF001D5C) else Color(0xFFE5E7EB)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)
    val cardBg = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(backgroundColor, backgroundEndColor)))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColorMain
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Coding Console 💻",
                    color = textColorMain,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Problem Selection Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Problem:",
                    color = textColorMain,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    codingProblems.forEachIndexed { index, prob ->
                        val selected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(if (selected) Color(0xFF8B3DFF) else cardBg)
                                .border(1.dp, if (selected) Color(0xFF8B3DFF) else Color(0xFF2D2D5E), RoundedCornerShape(50.dp))
                                .clickable { selectedIndex = index }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = prob.title,
                                color = if (selected) Color.White else textColorMain,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Problem Description Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = problem.title,
                        color = textColorMain,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = problem.description,
                        color = textColorMuted,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Code Editor
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Write your Solution:",
                    color = textColorMain,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = userCode,
                    onValueChange = { userCode = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF1F2937)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF8B3DFF)
                    )
                )
            }
        }

        // Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        isRunning = true
                        consoleOutput = "Compiling and running tests on AI Sandbox..."
                        scope.launch {
                            val evaluationPrompt = """
                                You are an automated online code judge.
                                Evaluate the following student code for the problem: "${problem.title}".
                                Problem Description: ${problem.description}
                                
                                Student Code:
                                ${userCode}
                                
                                Please test the code against typical inputs, check for compilation correctness, logic errors, and corner cases.
                                Return a summary of the evaluation. Your response should contain:
                                1. Status: SUCCESS if the code is correct, or COMPILE_ERROR / WRONG_ANSWER if there are issues.
                                2. Output/Feedback: Detailed feedback on correctness, efficiency, and any syntax/logical bugs.
                                Format your response in a clean, professional console-like output style.
                            """.trimIndent()

                            var response = GroqHelper.ask(evaluationPrompt)
                            if (response.startsWith("ERROR:") || response.trim().isEmpty()) {
                                throw Exception("Groq evaluation failed: ${response}")
                            }
                            consoleOutput = response
                            isRunning = false
                        }
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3DFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run & Evaluate")
                    }
                }

                OutlinedButton(
                    onClick = {
                        userCode = problem.starterCode
                        consoleOutput = "Press 'Run Code & Evaluate' to test your solution."
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                }
            }
        }

        // Output Console
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Console Output:",
                    color = textColorMain,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
                ) {
                    Text(
                        text = consoleOutput,
                        color = Color(0xFF00FF66), // matrix green
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PracticeCourseCard(
    course: PracticeCourse,
    onCodingClick: () -> Unit,
    onMcqClick: () -> Unit
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)

    Card(
        modifier=
            Modifier.fillMaxWidth(),
        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColor
            ),
        shape=
            RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                color=textColorMain,
                fontSize=22.sp,
                fontWeight=FontWeight.Bold
            )
            Spacer(
                Modifier.height(8.dp)
            )
            Text(
                text=course.subtitle,
                color=textColorMuted
            )
            Spacer(
                Modifier.height(15.dp)
            )
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ){
                Button(
                    onClick = onCodingClick,
                    colors=
                        ButtonDefaults.buttonColors(
                            containerColor=
                                Color(0xFF8B3DFF)
                        )
                ) {
                    Text("Coding")
                }

                Button(
                    onClick = onMcqClick,
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