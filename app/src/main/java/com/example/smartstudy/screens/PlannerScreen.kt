package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.StudyPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen() {
    val backend = remember { BackendProvider.backend }

    var subject by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("High") }
    var expanded by remember { mutableStateOf(false) }
    var plans by remember { mutableStateOf(emptyList<StudyPlan>()) }
    var error by remember { mutableStateOf<String?>(null) }

    val priorities = listOf("High", "Medium", "Low")
    val completedCount = plans.count { it.completed }
    val progress =
        if (plans.isEmpty())
            0f
        else
            completedCount.toFloat() / plans.size

    DisposableEffect(Unit) {
        val listener = backend.listenPlans(
            onUpdate = { plans = it },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            listener?.remove()
        }
    }

    Column(
        modifier =
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
                .padding(16.dp)
    ) {
        Spacer(
            Modifier.height(20.dp)
        )

        Text(
            "Study Planner",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
            },
            label = {
                Text("Subject")
            },
            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = time,
            onValueChange = {
                time = it
            },
            label = {
                Text("Study Time")
            },
            placeholder = {
                Text("09:00 AM")
            },
            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            Modifier.height(12.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = priority,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Priority")
                },
                modifier =
                    Modifier
                        .menuAnchor()
                        .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                priorities.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it)
                        },
                        onClick = {
                            priority = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            Modifier.height(16.dp)
        )

        Button(
            onClick = {
                if (subject.isNotBlank() && time.isNotBlank()) {
                    val isDuplicate = plans.any {
                        it.subject.equals(subject.trim(), ignoreCase = true) &&
                        it.time.equals(time.trim(), ignoreCase = true)
                    }
                    if (isDuplicate) {
                        error = "A task is already scheduled at this time"
                        return@Button
                    }
                    backend.addPlan(subject, time, priority) { result ->
                        result
                            .onSuccess {
                                subject = ""
                                time = ""
                            }
                            .onFailure { error = it.localizedMessage }
                    }
                }
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                "Add Plan"
            )
        }

        error?.let {
            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                it,
                color = Color(0xFFFFA8A8)
            )
        }

        Spacer(
            Modifier.height(18.dp)
        )

        Text(
            "Today's Progress",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(
            Modifier.height(8.dp)
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
            "$completedCount/${plans.size} Completed",
            color = Color(0xFF8B3DFF)
        )

        Spacer(
            Modifier.height(20.dp)
        )

        LazyColumn {
            items(plans, key = { it.id }) { plan ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 6.dp
                            ),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF1A2033)
                        ),
                    shape =
                        RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked =
                                    plan.completed,
                                onCheckedChange = {
                                    backend.setPlanCompleted(plan, it) { result ->
                                        result.onFailure { failure -> error = failure.localizedMessage }
                                    }
                                }
                            )

                            Spacer(
                                Modifier.width(8.dp)
                            )

                            Column {
                                Text(
                                    plan.subject,
                                    color = Color.White
                                )

                                Text(
                                    plan.time,
                                    color = Color.Gray
                                )
                            }
                        }

                        Text(
                            plan.priority,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
