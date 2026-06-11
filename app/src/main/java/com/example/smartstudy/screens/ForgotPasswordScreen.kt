package com.example.smartstudy.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen() {

    var email by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B1020),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Forgot Password",
            color = Color.White,
            fontSize = 30.sp
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {

                        Toast.makeText(
                            context,
                            "Reset link sent 😎",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    .addOnFailureListener {

                        Toast.makeText(
                            context,
                            "Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {

            Text("Send Reset Link")
        }
    }
}