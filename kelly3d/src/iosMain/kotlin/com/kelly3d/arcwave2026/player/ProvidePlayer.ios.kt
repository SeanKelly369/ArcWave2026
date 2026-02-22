package com.kelly3d.arcwave2026.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun providePlayer(): Player =
    remember { IosMediaPlayer() }