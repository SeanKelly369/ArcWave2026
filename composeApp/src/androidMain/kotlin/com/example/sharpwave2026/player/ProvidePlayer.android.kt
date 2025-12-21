package com.example.sharpwave2026.player

import com.example.sharpwave2026.AppContext

actual fun providePlayer(): Player = AndroidMediaPlayer(AppContext.app)