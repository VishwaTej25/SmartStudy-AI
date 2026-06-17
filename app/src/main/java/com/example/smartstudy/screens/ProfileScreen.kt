package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.UserProfile
import com.example.smartstudy.backend.UserSettings

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val backend = remember { BackendProvider.backend }
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var settings by remember { mutableStateOf(UserSettings()) }
    var error by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val profileListener = backend.listenProfile(
            onUpdate = { profile = it },
            onError = { error = it.localizedMessage }
        )
        val settingsListener = backend.listenSettings(
            onUpdate = { settings = it },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            profileListener?.remove()
            settingsListener?.remove()
        }
    }

    // If settings button was tapped, show SettingsScreen
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false }, onLogout = onLogout)
        return
    }

    // Theme-aware colors
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgStart = if (isDark) Color(0xFF050B1A) else Color(0xFFF3F4F6)
    val bgEnd = if (isDark) Color(0xFF0A1B55) else Color(0xFFE5E7EB)
    val cardBg = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)
    val textMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textMuted = if (isDark) Color.Gray else Color(0xFF6B7280)
    val accentColor = Color(0xFF8B3DFF)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(bgStart, bgEnd))
                )
                .padding(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            "Profile",
            color = textMain,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor,
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    profile?.fullName?.ifBlank { "Learner" } ?: "Learner",
                    color = textMain,
                    fontSize = 24.sp
                )

                Text(
                    profile?.email.orEmpty(),
                    color = textMuted
                )

                Text(
                    "Student Account",
                    color = accentColor
                )
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color(0xFFFFA8A8))
        }

        Spacer(Modifier.height(25.dp))

        ProfileItem(
            icon = Icons.Default.DarkMode,
            title = if (settings.darkMode) "Dark Mode: On" else "Dark Mode: Off",
            textColor = textMain,
            cardBg = cardBg
        ) {
            backend.updateSettings(settings.copy(darkMode = !settings.darkMode)) { result ->
                result.onFailure { error = it.localizedMessage }
            }
        }

        ProfileItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            textColor = textMain,
            cardBg = cardBg
        ) {
            showSettings = true
        }

        ProfileItem(
            icon = Icons.Default.EmojiEvents,
            title = "Leaderboard",
            textColor = textMain,
            cardBg = cardBg
        ) {}

        ProfileItem(
            icon = Icons.Default.Notifications,
            title = if (settings.notifications) "Notifications: On" else "Notifications: Off",
            textColor = textMain,
            cardBg = cardBg
        ) {
            backend.updateSettings(settings.copy(notifications = !settings.notifications)) { result ->
                result.onFailure { error = it.localizedMessage }
            }
        }

        ProfileItem(
            icon = Icons.Default.Logout,
            title = "Logout",
            textColor = textMain,
            cardBg = cardBg
        ) {
            onLogout()
        }
    }
}

@Composable
fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color = Color.White,
    cardBg: Color = Color(0xFF1A2033),
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() },
        colors =
            CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = Color(0xFF8B3DFF)
            )

            Spacer(Modifier.width(15.dp))

            Text(
                title,
                color = textColor
            )
        }
    }
}
