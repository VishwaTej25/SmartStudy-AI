package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.smartstudy.backend.Course
import com.example.smartstudy.backend.SmartStudyBackend

@Composable
fun CoursesScreen() {
    val backend = remember { BackendProvider.backend }

    var selectedCourse by remember {
        mutableStateOf<Course?>(null)
    }
    var courses by remember {
        mutableStateOf(SmartStudyBackend.defaultCourses)
    }
    var enrolledCourseIds by remember {
        mutableStateOf(emptySet<String>())
    }
    var error by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(Unit) {
        val coursesListener = backend.listenCourses(
            onUpdate = { courses = it },
            onError = { error = it.localizedMessage }
        )
        val enrollmentListener = backend.listenEnrollments(
            onUpdate = { enrolledCourseIds = it.keys },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            coursesListener.remove()
            enrollmentListener?.remove()
        }
    }

    if (selectedCourse != null) {
        CourseDetailsScreen()
    } else {
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
        ) {
            Text(
                text = "Courses",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            error?.let {
                Text(
                    text = it,
                    color = Color(0xFFFFA8A8),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(courses) { course ->
                    CourseCard(
                        course = course,
                        enrolled = enrolledCourseIds.contains(course.id),
                        onClick = {
                            selectedCourse = course
                        },
                        onEnrollmentChange = { checked ->
                            backend.setEnrollment(course, checked) { result ->
                                result.onFailure { error = it.localizedMessage }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    enrolled: Boolean,
    onClick: () -> Unit,
    onEnrollmentChange: (Boolean) -> Unit
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(230.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF1B2235)
            ),
        shape =
            RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement =
                Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = course.emoji,
                    fontSize = 34.sp,
                    color = Color.White
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                Text(
                    text = course.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    Modifier.height(6.dp)
                )

                Text(
                    text = course.subtitle,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    onEnrollmentChange(!enrolled)
                },
                modifier =
                    Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (enrolled)
                                Color(0xFF00B894)
                            else
                                Color(0xFF8B3DFF)
                    )
            ) {
                Text(
                    if (enrolled)
                        "Enrolled"
                    else
                        "Enroll"
                )
            }
        }
    }
}
