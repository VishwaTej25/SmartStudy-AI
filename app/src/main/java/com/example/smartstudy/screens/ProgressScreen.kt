package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.ProgressSummary
import com.example.smartstudy.backend.SmartStudyBackend

@Composable
fun ProgressScreen() {
    val backend = remember { BackendProvider.backend }
    var courses by remember { mutableStateOf(SmartStudyBackend.defaultCourses) }
    var summary by remember { mutableStateOf(ProgressSummary()) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        var progressListener = backend.listenProgress(
            courses = courses,
            onUpdate = { summary = it },
            onError = { error = it.localizedMessage }
        )

        val coursesListener = backend.listenCourses(
            onUpdate = { remoteCourses ->
                courses = remoteCourses
                progressListener?.remove()
                progressListener = backend.listenProgress(
                    courses = remoteCourses,
                    onUpdate = { summary = it },
                    onError = { error = it.localizedMessage }
                )
            },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            coursesListener.remove()
            progressListener?.remove()
        }
    }

    Column(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
    ) {
        Text(
            text = "Progress Dashboard",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        error?.let {
            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = it,
                color = Color(0xFFFFA8A8)
            )
        }

        Spacer(
            Modifier.height(20.dp)
        )

        ProgressCard(
            "Courses Enrolled",
            summary.coursesEnrolled.toString()
        )

        Spacer(
            Modifier.height(12.dp)
        )

        ProgressCard(
            "Tests Attempted",
            summary.testsAttempted.toString()
        )

        Spacer(
            Modifier.height(12.dp)
        )

        ProgressCard(
            "Average Score",
            "${summary.averageScore}%"
        )

        Spacer(
            Modifier.height(12.dp)
        )

        ProgressCard(
            "Learning Streak",
            "${summary.learningStreak} Days"
        )

        Spacer(
            Modifier.height(20.dp)
        )

        Text(
            text = "Course Progress",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(15.dp)
        )

        if (summary.courseProgress.isEmpty()) {
            Text(
                text = "Enroll in a course to start tracking progress.",
                color = Color.LightGray
            )
        } else {
            summary.courseProgress.forEach { (courseName, progress) ->
                CourseProgressCard(
                    courseName,
                    progress.toFloat()
                )

                Spacer(
                    Modifier.height(12.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressCard(
    title: String,
    value: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF1B2235)
            ),
        shape =
            RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 16.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CourseProgressCard(
    courseName: String,
    progress: Float
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF1B2235)
            ),
        shape =
            RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {
            Text(
                text = courseName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = "${(progress * 100).toInt()}% Completed",
                color = Color.LightGray
            )
        }
    }
}
