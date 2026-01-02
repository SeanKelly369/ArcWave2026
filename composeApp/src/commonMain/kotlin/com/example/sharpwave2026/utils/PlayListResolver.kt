package com.example.sharpwave2026.utils

import com.example.sharpwave2026.player.Track

fun resolveTracksByUri(trackUris: List<String>, allTracks: List<Track>): List<Track> {
    val map = allTracks.associateBy { it.uri }
    return trackUris.mapNotNull { map[it] }
}