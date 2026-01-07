package com.example.sharpwave2026.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun providePlayer(): Player {
    val ctx = LocalContext.current.applicationContext
    return remember(ctx) { AndroidMediaPlayer(context = ctx) }
}