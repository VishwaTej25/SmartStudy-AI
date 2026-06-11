package com.example.smartstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.smartstudy.backend.ChatEntry

@Composable
fun ChatScreen() {
    val backend = remember { BackendProvider.backend }

    var message by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatEntry(
                    text = "Hello. Ask me any study doubt!",
                    userMessage = false
                )
            )
        )
    }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = backend.listenChat(
            onUpdate = { remoteMessages ->
                messages =
                    if (remoteMessages.isEmpty())
                        listOf(ChatEntry(text = "Hello. Ask me any study doubt!", userMessage = false))
                    else
                        remoteMessages
            },
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
                            Color(0xFF000814),
                            Color(0xFF001D5C)
                        )
                    )
                )
    ) {
        Text(
            text = "SmartStudy AI",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier.padding(16.dp)
        )

        error?.let {
            Text(
                text = it,
                color = Color(0xFFFFA8A8),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
        ) {
            items(messages, key = { it.id.ifBlank { it.text } }) { chat ->
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (chat.userMessage)
                            Arrangement.End
                        else
                            Arrangement.Start
                ) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    if (chat.userMessage)
                                        Color(0xFF8B5CF6)
                                    else
                                        Color(0xFF1B2235)
                            ),
                        shape =
                            RoundedCornerShape(16.dp),
                        modifier =
                            Modifier
                                .padding(8.dp)
                                .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = chat.text,
                            color = Color.White,
                            modifier =
                                Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                placeholder = {
                    Text("Ask your doubt...")
                },
                modifier =
                    Modifier.weight(1f),
                shape =
                    RoundedCornerShape(20.dp)
            )

            Spacer(
                Modifier.width(10.dp)
            )

            FloatingActionButton(
                onClick = {
                    if (message.isNotBlank()) {
                        val outgoing = message
                        message = ""
                        backend.sendChatMessage(outgoing) { result ->
                            result.onFailure { error = it.localizedMessage }
                        }
                    }
                },
                containerColor =
                    Color(0xFF8B5CF6)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
