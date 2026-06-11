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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.UserSettings

@Composable
fun SettingsScreen(
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

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0B1020),
            Color(0xFF121826),
            Color(0xFF1E1B4B)
        )
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
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color(0xFF7C3AED)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Settings",
                color = Color.White,
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
            onCheckedChange = {
                backend.updateSettings(settings.copy(darkMode = it)) { result ->
                    result.onFailure { failure -> error = failure.localizedMessage }
                }
            }
        )

        SettingsCard(
            title = "AI Voice Assistant",
            checked = settings.aiVoice,
            onCheckedChange = {
                backend.updateSettings(settings.copy(aiVoice = it)) { result ->
                    result.onFailure { failure -> error = failure.localizedMessage }
                }
            }
        )

        SettingsCard(
            title = "Smart Notifications",
            checked = settings.notifications,
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
                containerColor = Color(0xFF7C3AED)
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
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
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
                color = Color.White,
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
