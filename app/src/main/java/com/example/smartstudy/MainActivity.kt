package com.example.smartstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.UserSettings
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

            // Listen to Firestore settings to sync dark mode
            DisposableEffect(isLoggedIn) {
                if (!isLoggedIn) {
                    onDispose { }
                } else {
                    val reg = BackendProvider.backend.listenSettings(
                        onUpdate = { settings ->
                            darkTheme = settings.darkMode
                        },
                        onError = { /* ignore */ }
                    )
                    onDispose { reg?.remove() }
                }
            }

            MaterialTheme(

                colorScheme =
                    if(darkTheme)
                        darkColorScheme(
                            primary=Color(0xFF7C3AED)
                        )
                    else
                        lightColorScheme(
                            primary=Color(0xFF7C3AED)
                        )

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
                                val newDark = !darkTheme
                                darkTheme = newDark
                                // Also persist to Firestore
                                BackendProvider.backend.updateSettings(
                                    UserSettings(darkMode = newDark)
                                )
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
