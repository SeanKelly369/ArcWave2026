package com.kelly3d.arcwave2026.library

import androidx.compose.runtime.Composable
import com.kelly3d.arcwave2026.player.Track

interface AudioLibrary {
    suspend fun scanTracks(): List<Track>
}

@Composable
expect fun provideAudioLibrary(): AudioLibrary