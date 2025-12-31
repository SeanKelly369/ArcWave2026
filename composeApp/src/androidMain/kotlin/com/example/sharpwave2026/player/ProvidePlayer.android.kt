package com.example.sharpwave2026.player

import com.example.sharpwave2026.library.AndroidAppContext

actual fun providePlayer(): Player = AndroidMediaPlayer(AndroidAppContext.app)