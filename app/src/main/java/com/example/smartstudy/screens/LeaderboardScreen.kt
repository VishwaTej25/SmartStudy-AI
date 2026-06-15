package com.example.smartstudy.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.smartstudy.backend.SmartStudyBackend
import com.example.smartstudy.backend.UserProfile
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun LeaderboardScreen() {
    val backend = remember { BackendProvider.backend }
    var users by remember { mutableStateOf(SmartStudyBackend.sampleLeaderboard) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = backend.listenLeaderboard(
            onUpdate = { users = it },
            onError = { error = it.localizedMessage }
        )

        onDispose {
            listener.remove()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1020),
                        Color(0xFF121826),
                        Color(0xFF1E1B4B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Leaderboard",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Top performing learners",
                color = Color.Gray,
                fontSize = 16.sp
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color(0xFFFFA8A8),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            LazyColumn {
                itemsIndexed(users) { index, user ->
                    LeaderboardCard(
                        rank = index + 1,
                        user = user
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
private fun LeaderboardCard(
    rank: Int,
    user: UserProfile
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color(0xFF7C3AED),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.fullName.ifBlank { user.email.ifBlank { "Learner" } },
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "${user.xp} XP - ${user.streak} Day Streak",
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "I am Rank $rank on SmartStudy AI with ${user.xp} XP! Join me in learning! 🚀")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Rank",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector =
                    Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFACC15),
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
