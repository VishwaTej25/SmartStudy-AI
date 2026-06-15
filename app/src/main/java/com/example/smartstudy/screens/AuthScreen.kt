package com.example.smartstudy.screens

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val backend = remember { BackendProvider.backend }
    val context = LocalContext.current

    var isLogin by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0A14),
                        Color(0xFF10102A),
                        Color(0xFF1A0A2E)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── App Icon ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7C3AED), Color(0xFF5B21B6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("→", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SMART STUDY AI",
                color = Color(0xFF7C3AED),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLogin) "Welcome back" else "Create Account",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isLogin) "Log in to your account" else "Start your learning journey",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Form Card ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14142A)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Sign-up only fields
                    if (!isLogin) {
                        AuthTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            placeholder = "Your full name"
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        AuthTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = "Mobile Number",
                            placeholder = "10-digit mobile"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Email
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "you@example.com"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true
                    )

                    if (isLogin) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                "Forgot password?",
                                color = Color(0xFF7C3AED),
                                fontSize = 13.sp,
                                modifier = Modifier.clickable { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login / Sign up button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                message = "Email and password are required."
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isLogin && fullName.isBlank()) {
                                message = "Full name is required."
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            message = null

                            val onResult: (Result<Unit>) -> Unit = { result ->
                                isLoading = false
                                result
                                    .onSuccess { onLoginSuccess() }
                                    .onFailure {
                                        val errMsg = it.localizedMessage ?: "Authentication failed."
                                        message = errMsg
                                        Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
                                    }
                            }

                            if (isLogin) {
                                backend.signIn(email, password, onResult)
                            } else {
                                backend.signUp(fullName, mobile, email, password, onResult)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED),
                            disabledContainerColor = Color(0xFF4C2889)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = if (isLogin) "Log in" else "Create Account",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Error message
            message?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    it,
                    color = Color(0xFFFCA5A5),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Toggle login / signup
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLogin) "Don't have an account? " else "Already have an account? ",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )
                Text(
                    text = if (isLogin) "Create one" else "Log in",
                    color = Color(0xFF7C3AED),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { isLogin = !isLogin }
                )
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF4B5563)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF0A0A1F),
                focusedContainerColor = Color(0xFF0A0A1F),
                unfocusedBorderColor = Color(0xFF2D2D5E),
                focusedBorderColor = Color(0xFF7C3AED),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            )
        )
    }
}
