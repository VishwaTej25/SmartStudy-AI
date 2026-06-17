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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import com.example.smartstudy.backend.UserSettings

@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
    onLogout: () -> Unit
) {
    val backend = remember { BackendProvider.backend }
    var settings by remember { mutableStateOf(UserSettings()) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = backend.listenSettings(
            onUpdate = { settings = it },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            listener?.remove()
        }
    }

    // Theme-aware colors
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgStart = if (isDark) Color(0xFF050B1A) else Color(0xFFF3F4F6)
    val bgEnd = if (isDark) Color(0xFF0A1B55) else Color(0xFFE5E7EB)
    val cardBg = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)
    val textMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textMuted = if (isDark) Color.Gray else Color(0xFF6B7280)
    val accentColor = Color(0xFF8B3DFF)

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(bgStart, bgEnd)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textMain
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = accentColor
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Settings",
                color = textMain,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color(0xFFFFA8A8)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        SettingsCard(
            title = "Dark Mode",
            checked = settings.darkMode,
            textColor = textMain,
            cardBg = cardBg,
            onCheckedChange = {
                backend.updateSettings(settings.copy(darkMode = it)) { result ->
                    result.onFailure { failure -> error = failure.localizedMessage }
                }
            }
        )

        SettingsCard(
            title = "AI Voice Assistant",
            checked = settings.aiVoice,
            textColor = textMain,
            cardBg = cardBg,
            onCheckedChange = {
                backend.updateSettings(settings.copy(aiVoice = it)) { result ->
                    result.onFailure { failure -> error = failure.localizedMessage }
                }
            }
        )

        SettingsCard(
            title = "Smart Notifications",
            checked = settings.notifications,
            textColor = textMain,
            cardBg = cardBg,
            onCheckedChange = {
                backend.updateSettings(settings.copy(notifications = it)) { result ->
                    result.onFailure { failure -> error = failure.localizedMessage }
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Logout",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    checked: Boolean,
    textColor: Color,
    cardBg: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
