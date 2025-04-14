package com.example.armoryboxkotline

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*

@Composable
fun DecksScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Decks Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}