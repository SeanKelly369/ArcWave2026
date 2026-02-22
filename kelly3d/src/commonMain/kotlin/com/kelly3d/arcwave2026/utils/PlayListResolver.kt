package com.kelly3d.arcwave2026.utils

import com.kelly3d.arcwave2026.player.Track

fun resolveTracksByUri(trackUris: List<String>, allTracks: List<Track>): List<Track> {
    val map = allTracks.associateBy { it.uri }
    return trackUris.mapNotNull { map[it] }
}