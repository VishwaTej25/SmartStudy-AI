package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.Course
import com.example.smartstudy.backend.Enrollment
import com.example.smartstudy.backend.SmartStudyBackend

@Composable
fun CoursesScreen() {
    val backend = remember { BackendProvider.backend }

    // selectedCourse → show CourseTopicsScreen (topics list)
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    // selectedDetailCourse → show CourseDetailsScreen (full detail / enroll)
    var selectedDetailCourse by remember { mutableStateOf<Course?>(null) }

    if (selectedDetailCourse != null) {
        CourseDetailsScreen(
            course = selectedDetailCourse!!,
            onBack = { selectedDetailCourse = null }
        )
        return
    }

    if (selectedCourse != null) {
        CourseTopicsScreen(
            course = selectedCourse!!,
            onBack = { selectedCourse = null }
        )
        return
    }

    var courses by remember { mutableStateOf(SmartStudyBackend.defaultCourses) }
    var enrollments by remember { mutableStateOf<Map<String, Enrollment>>(emptyMap()) }
    val enrolledCourseIds = enrollments.keys
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0=All, 1=Enrolled

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

    val filteredCourses = courses.filter { course ->
        val matchesSearch = course.title.contains(searchQuery, ignoreCase = true) ||
                course.subtitle.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            1 -> enrolledCourseIds.contains(course.id)
            else -> true
        }
        matchesSearch && matchesTab
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0A0A14) else Color(0xFFF3F4F6)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val inputBgColor = if (isDark) Color(0xFF14142A) else Color(0xFFFFFFFF)
    val inputBorderColor = if (isDark) Color(0xFF2D2D5E) else Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Courses",
                color = textColorMain,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${courses.size} B.Tech courses available",
                color = Color(0xFF7C3AED),
                fontSize = 13.sp
            )
        }

        // ── Search ───────────────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search courses...", color = Color(0xFF6B7280)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF6B7280)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = inputBgColor,
                focusedContainerColor = inputBgColor,
                unfocusedBorderColor = inputBorderColor,
                focusedBorderColor = Color(0xFF7C3AED),
                unfocusedTextColor = textColorMain,
                focusedTextColor = textColorMain
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Tabs ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabChip(label = "All", selected = selectedTab == 0, onClick = { selectedTab = 0 })
            TabChip(
                label = "Enrolled (${enrolledCourseIds.size})",
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        error?.let {
            Text(
                text = it,
                color = Color(0xFFFFA8A8),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 13.sp
            )
        }

        // ── Course List ───────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCourses) { course ->
                val isEnrolled = enrolledCourseIds.contains(course.id)
                val progress = enrollments[course.id]?.progress ?: 0.0
                CourseListCard(
                    course = course,
                    enrolled = isEnrolled,
                    progress = progress,
                    // Card click → open topic list
                    onClick = { selectedCourse = course },
                    // Details button → open details screen
                    onDetailsClick = { selectedDetailCourse = course },
                    onEnrollmentChange = { checked ->
                        backend.setEnrollment(course, checked) { result ->
                            result.onFailure { error = it.localizedMessage }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Tab Chip ─────────────────────────────────────────────────────────────────
@Composable
fun TabChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val unselectedBg = if (isDark) Color(0xFF14142A) else Color(0xFFFFFFFF)
    val unselectedBorder = if (isDark) Color(0xFF2D2D5E) else Color(0xFFE5E7EB)

    val bgColor = if (selected) Color(0xFF7C3AED) else unselectedBg
    val textColor = if (selected) Color.White else (if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563))
    val borderColor = if (selected) Color(0xFF7C3AED) else unselectedBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── Accent color palette ──────────────────────────────────────────────────────
val courseAccentColors = listOf(
    Color(0xFFEF4444), Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B),
    Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF06B6D4), Color(0xFFF97316),
    Color(0xFF84CC16), Color(0xFF6366F1), Color(0xFFD946EF), Color(0xFF14B8A6),
    Color(0xFF0EA5E9)
)

// ── Course Card ───────────────────────────────────────────────────────────────
@Composable
fun CourseListCard(
    course: Course,
    enrolled: Boolean,
    progress: Double = 0.0,
    onClick: () -> Unit,
    onDetailsClick: () -> Unit = onClick,
    onEnrollmentChange: (Boolean) -> Unit
) {
    val accentColor = courseAccentColors[
        (SmartStudyBackend.defaultCourses.indexOfFirst { it.id == course.id }
            .takeIf { it >= 0 } ?: 0) % courseAccentColors.size
    ]

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0xFF14142A) else Color(0xFFFFFFFF)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563)
    val textColorSecondary = if (isDark) Color(0xFF6B7280) else Color(0xFF6B7280)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = accentColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Accent top stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Emoji badge
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(course.emoji, fontSize = 26.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        color = textColorMain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = course.subtitle, color = textColorMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (enrolled) "Enrolled ✓" else "Tap to view details",
                        color = if (enrolled) accentColor else textColorSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (enrolled) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            // Progress bar (enrolled only)
            if (enrolled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Course Progress", color = textColorMuted, fontSize = 11.sp)
                        Text(
                            "${(progress * 100).toInt()}%",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.2f)
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (enrolled) {
                    // Details button
                    Button(
                        onClick = onDetailsClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Details", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    // Reject / Unenroll button
                    OutlinedButton(
                        onClick = { onEnrollmentChange(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Text("Unenroll", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    // Enroll button
                    Button(
                        onClick = { onEnrollmentChange(true) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Enroll Now", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Backward-compat alias ─────────────────────────────────────────────────────
@Composable
fun CourseCard(
    course: Course,
    enrolled: Boolean,
    progress: Double = 0.0,
    onClick: () -> Unit,
    onEnrollmentChange: (Boolean) -> Unit
) {
    CourseListCard(
        course = course,
        enrolled = enrolled,
        progress = progress,
        onClick = onClick,
        onEnrollmentChange = onEnrollmentChange
    )
}
