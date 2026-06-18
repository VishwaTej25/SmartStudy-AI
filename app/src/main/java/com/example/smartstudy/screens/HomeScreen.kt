package com.example.smartstudy.screens

import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(onNavigate: (Int) -> Unit = {}) {
    HomeContent(onNavigate = onNavigate)
}