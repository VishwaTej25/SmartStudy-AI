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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.Course
import com.example.smartstudy.backend.Enrollment
import com.example.smartstudy.backend.SmartStudyBackend

@Composable
fun AssessmentScreen() {
    val backend = remember { BackendProvider.backend }
    var courses by remember { mutableStateOf(SmartStudyBackend.defaultCourses) }
    var enrollments by remember { mutableStateOf<Map<String, Enrollment>>(emptyMap()) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    DisposableEffect(Unit) {
        val coursesListener = backend.listenCourses(
            onUpdate = { courses = it },
            onError = { error = it.localizedMessage }
        )
        val enrollmentListener = backend.listenEnrollments(
            onUpdate = { enrollments = it },
            onError = { error = it.localizedMessage }
        )
        onDispose {
            coursesListener.remove()
            enrollmentListener?.remove()
        }
    }

    val enrolledCourses = courses.filter { enrollments.containsKey(it.id) }

    if (selectedCourse != null) {
        TopicTestScreen(
            courseName = selectedCourse!!.title,
            courseId = selectedCourse!!.id,
            onBack = {
                selectedCourse = null
            }
        )
        return
    }

    LazyColumn(
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
        item {
            Text(
                text = "Assessments 📝",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Attempt tests from enrolled courses",
                color = Color.LightGray
            )
            Spacer(Modifier.height(25.dp))
        }

        error?.let {
            item {
                Text(it, color = Color(0xFFFFA8A8), modifier = Modifier.padding(bottom = 12.dp))
            }
        }

        if (enrolledCourses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2235)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No enrolled courses found",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Please go to the Courses tab to enroll in a course first!",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(enrolledCourses) { course ->
                AssessmentCard(
                    course = course,
                    onAssessmentClick = {
                        selectedCourse = course
                    }
                )
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}

@Composable
fun AssessmentCard(
    course: Course,
    onAssessmentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2235)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = course.emoji,
                fontSize = 40.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = course.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = course.subtitle,
                color = Color.LightGray
            )
            Spacer(Modifier.height(15.dp))
            Button(
                onClick = onAssessmentClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894))
            ) {
                Text("Start Assessment", color = Color.White)
            }
        }
    }
}