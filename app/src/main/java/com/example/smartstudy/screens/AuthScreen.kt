package com.example.smartstudy.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider

@Composable
fun AuthScreen(
    onLoginSuccess:()->Unit
){
    val backend = remember { BackendProvider.backend }

    var isLogin by remember {
        mutableStateOf(true)
    }

    var fullName by remember {
        mutableStateOf("")
    }

    var mobile by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier=
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
    ){

        Column(

            modifier=
                Modifier
                    .fillMaxSize()
                    .padding(28.dp),

            verticalArrangement=
                Arrangement.Center

        ){

            Text(

                text=
                    if(isLogin)
                        "Welcome Back 👋"
                    else
                        "Create Account",

                color=Color.White,
                fontSize=34.sp,
                fontWeight=FontWeight.Bold

            )

            Spacer(
                Modifier.height(30.dp)
            )

            if(!isLogin){

                OutlinedTextField(
                    value=fullName,

                    onValueChange={
                        fullName=it
                    },

                    label={
                        Text("Full Name")
                    },

                    modifier=
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value=mobile,

                    onValueChange={
                        mobile=it
                    },

                    label={
                        Text("Mobile Number")
                    },

                    modifier=
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    Modifier.height(12.dp)
                )

            }

            OutlinedTextField(

                value=email,

                onValueChange={
                    email=it
                },

                label={
                    Text("Email")
                },

                modifier=
                    Modifier.fillMaxWidth()

            )

            Spacer(
                Modifier.height(12.dp)
            )

            OutlinedTextField(

                value=password,

                onValueChange={
                    password=it
                },

                visualTransformation=
                    PasswordVisualTransformation(),

                label={
                    Text("Password")
                },

                modifier=
                    Modifier.fillMaxWidth()

            )

            Spacer(
                Modifier.height(8.dp)
            )

            if(isLogin){

                Text(
                    "Forgot Password?",
                    color=Color(0xFF8B3DFF),

                    modifier=
                        Modifier.clickable { }
                )

            }

            Spacer(
                Modifier.height(25.dp)
            )

            Button(

                onClick={
                    if (email.isBlank() || password.isBlank()) {
                        message = "Email and password are required."
                        return@Button
                    }

                    if (!isLogin && fullName.isBlank()) {
                        message = "Full name is required."
                        return@Button
                    }

                    isLoading = true
                    message = null

                    val onResult: (Result<Unit>) -> Unit = { result ->
                        isLoading = false
                        result
                            .onSuccess { onLoginSuccess() }
                            .onFailure { message = it.localizedMessage ?: "Authentication failed." }
                    }

                    if (isLogin) {
                        backend.signIn(email, password, onResult)
                    } else {
                        backend.signUp(fullName, mobile, email, password, onResult)
                    }

                },

                enabled = !isLoading,

                modifier=
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                shape=
                    RoundedCornerShape(
                        16.dp
                    )

            ){

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier=Modifier.size(22.dp),
                        strokeWidth=2.dp,
                        color=Color.White
                    )
                } else {
                    Text(
                        if(isLogin)
                            "Login"
                        else
                            "Create Account"
                    )
                }

            }

            message?.let {
                Spacer(
                    Modifier.height(10.dp)
                )

                Text(
                    it,
                    color=Color(0xFFFFA8A8),
                    fontSize=14.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            TextButton(

                onClick={

                    isLogin=!isLogin

                }

            ){

                Text(

                    if(isLogin)
                        "Create account?"
                    else
                        "Already have account?"

                )

            }

        }

    }

}
