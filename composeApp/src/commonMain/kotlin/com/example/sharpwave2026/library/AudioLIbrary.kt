package com.example.sharpwave2026.library

import com.example.sharpwave2026.player.Track

interface AudioLibrary {
    suspend fun scanTracks(): List<Track>
}

expect fun provideAudioLibrary(): AudioLibrary