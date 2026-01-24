package com.example.arcwave2026.library

import com.example.arcwave2026.player.Track

object PlaylistBuilder {

    fun buildAuto(tracks: List<Track>): List<PlayList> {
        val clean = tracks.filter { it.uri.isNotBlank() }

        fun toUris(list: List<Track>): List<String> = list.map { it.uri }.distinct()


        val all = PlayList(
            id = "all",
            name = "All Tracks",
            trackUris = toUris(clean),
            kind = PlayList.Kind.Auto
        )

        val audiobooks = PlayList(
            id = "audiobooks",
            name = "Audiobooks",
            trackUris = toUris(clean.filter { (it.durationMs ?: 0L) >= 30L * 60L * 1000L }),
            kind = PlayList.Kind.Auto
        )

        val byArtist = clean
            .groupBy { it.artist.ifBlank { "Unknown Artist" } }
            .toList()
            .sortedBy { (artist, _) -> artist.lowercase() }
            .map { (artist, items) ->
                PlayList(
                    id = "artist:${artist.lowercase()}",
                    name = artist,
                    trackUris = toUris(items),
                    kind = PlayList.Kind.Auto
                )
            }

        return listOf(all, audiobooks) + byArtist
    }
}