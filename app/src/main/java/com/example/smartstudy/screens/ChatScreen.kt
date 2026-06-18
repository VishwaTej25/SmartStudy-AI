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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.ChatEntry
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.smartstudy.backend.GroqHelper
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ChatScreen() {
    val backend = remember { BackendProvider.backend }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF000814) else Color(0xFFF3F4F6)
    val backgroundEndColor = if (isDark) Color(0xFF001D5C) else Color(0xFFE5E7EB)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val aiCardBg = if (isDark) Color(0xFF1B2235) else Color(0xFFE5E7EB)
    val aiCardText = if (isDark) Color.White else Color(0xFF1F2937)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            backgroundColor,
                            backgroundEndColor
                        )
                    )
                )
    ) {
        Text(
            text = "SmartStudy AI",
            color = textColorMain,
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
            state = listState,
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
                                        aiCardBg
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
                            color = if (chat.userMessage) Color.White else aiCardText,
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

                        messages = messages + ChatEntry(
                            text = outgoing,
                            userMessage = true
                        )

                        messages = messages + ChatEntry(
                            text = "🤖 Thinking...",
                            userMessage = false
                        )

                        scope.launch {
                            val aiReply = GroqHelper.ask(outgoing)

                            messages = messages + ChatEntry(
                                text = aiReply,
                                userMessage = false
                            )

                            android.util.Log.d("GROQ_REPLY", aiReply)
                        }
                    }
                },
                containerColor = Color(0xFF8B5CF6)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
