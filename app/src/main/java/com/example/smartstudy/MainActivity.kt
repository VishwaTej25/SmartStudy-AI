package com.example.smartstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.navigation.BottomNavBar
import com.example.smartstudy.screens.AuthScreen
import com.example.smartstudy.screens.SplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            var showSplash by remember {
                mutableStateOf(true)
            }

            var isLoggedIn by remember {
                mutableStateOf(BackendProvider.backend.currentUserId != null)
            }

            var darkTheme by remember {
                mutableStateOf(true)
            }

            MaterialTheme(

                colorScheme =
                    if(darkTheme)
                        darkColorScheme(
                            primary=Color(0xFF7C3AED)
                        )
                    else
                        lightColorScheme()

            ){

                when{

                    showSplash -> {

                        SplashScreen(

                            onSplashFinished = {

                                showSplash=false

                            }

                        )

                    }

                    !isLoggedIn -> {

                        AuthScreen(

                            onLoginSuccess = {

                                isLoggedIn=true

                            }

                        )

                    }

                    else -> {

                        BottomNavBar(

                            darkTheme=darkTheme,

                            onThemeChange={

                                darkTheme=!darkTheme

                            },

                            onLogout={

                                BackendProvider.backend.signOut()
                                isLoggedIn=false

                            }

                        )

                    }

                }

            }

        }

    }

}
