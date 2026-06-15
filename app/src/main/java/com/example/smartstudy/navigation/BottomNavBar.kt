package com.example.smartstudy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.smartstudy.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    onLogout: () -> Unit
) {

    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    val drawerState =
        rememberDrawerState(
            initialValue = DrawerValue.Closed
        )

    val scope =
        rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            AppDrawer(
                onItemClick = {
                    when(it){
                        "Home" -> {
                            selectedItem = 0
                        }
                        "Courses" -> {
                            selectedItem = 1
                        }
                        "AI" -> {
                            selectedItem = 2
                        }
                        "Profile" -> {
                            selectedItem = 3
                        }
                        "Assessment" -> {
                            selectedItem = 4
                        }
                        "Practice" -> {
                            selectedItem = 5
                        }
                        "Planner" -> {
                            selectedItem = 6
                        }
                        "Leaderboard" -> {
                            selectedItem = 7
                        }
                                                "Theory" -> {
                            selectedItem = 9
                        }
                        "Logout" -> {
                            onLogout()
                        }
                    }
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Smart Study"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Text("☰")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { selectedItem = 1 }
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Search,
                                contentDescription =
                                    "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { selectedItem = 10 }
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Notifications,
                                contentDescription =
                                    "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                onThemeChange()
                            }
                        ) {
                            Text(
                                if(darkTheme)
                                    "🌙"
                                else
                                    "☀"
                            )
                        }
                        IconButton(
                            onClick = {
                                selectedItem = 3
                            }
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.AccountCircle,
                                contentDescription =
                                    "Profile",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor =
                        Color(0xFF111827)
                ) {
                    NavigationBarItem(
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedItem == 1,
                        onClick = { selectedItem = 1 },
                        icon = { Icon(Icons.Default.Book, contentDescription = null) },
                        label = { Text("Courses") }
                    )
                    NavigationBarItem(
                        selected = selectedItem == 5,
                        onClick = { selectedItem = 5 },
                        icon = { Icon(Icons.Default.Code, contentDescription = null) },
                        label = { Text("Practice") }
                    )
                    NavigationBarItem(
                        selected = selectedItem == 4,
                        onClick = { selectedItem = 4 },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                        label = { Text("Assessment") }
                    )
                    NavigationBarItem(
                        selected = selectedItem == 3,
                        onClick = { selectedItem = 3 },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") }
                    )
                }
            }
        ) { paddingValues ->
            Surface(
                modifier =
                    Modifier.padding(
                        paddingValues
                    )
            ) {
                when(selectedItem){
                    0 -> HomeScreen()
                    1 -> CoursesScreen()
                    2 -> ChatScreen()
                    3 -> ProfileScreen(onLogout = onLogout)
                    4 -> AssessmentScreen()
                    5 -> PracticeScreen()
                    6 -> PlannerScreen()
                    7 -> LeaderboardScreen()
                    8 -> SettingsScreen(onLogout = onLogout)
                    9 -> TheoryQuestionsScreen(onBack = { selectedItem = 0 })
                    10 -> NotificationScreen()
                }
            }
        }
    }
}
