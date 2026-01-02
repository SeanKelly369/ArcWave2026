package com.example.sharpwave2026.library

import com.example.sharpwave2026.player.Track

data class PlayList(
    val id: String,
    val name: String,
    val trackUris: List<String>,
    val kind: Kind = Kind.Manual
) {
    enum class Kind { Auto, Manual }
}
