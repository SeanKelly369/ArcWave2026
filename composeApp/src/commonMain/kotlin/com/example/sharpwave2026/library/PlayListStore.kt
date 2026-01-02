package com.example.sharpwave2026.library

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayListStore {

    private val _playlists = MutableStateFlow<List<PlayList>>(emptyList())
    val playlists: StateFlow<List<PlayList>> = _playlists.asStateFlow()

    fun createManual(name: String): PlayList {
        val pl = PlayList(
            id = newId(),
            name = name.trim().ifEmpty { "New playlist" },
            trackUris = emptyList(),
            kind = PlayList.Kind.Manual
        )
        _playlists.value += _playlists.value + pl
        return pl
    }

    fun saveQueueAsPlaylist(name: String, queue: List<String>): PlayList {
        val pl = PlayList(
            id = newId(),
            name = name.trim().ifEmpty { "Queue" },
            trackUris = queue.distinct(),
            kind = PlayList.Kind.Manual
        )
        _playlists.value += _playlists.value + pl
        return pl
    }

    fun delete(id: String) {
        _playlists.value = _playlists.value.filterNot { it.id == id }
    }

    fun rename(id: String, name: String) {
        val n = name.trim()
        if (n.isEmpty()) return
        _playlists.value = _playlists.value.map { if (it.id == id) it.copy(name = n) else it }
    }

    fun setTracks(id: String, trackUris: List<String>) {
        _playlists.value = _playlists.value.map { if (it.id == id) it.copy(trackUris = trackUris) else it }
    }

    private fun newId(): String = "pl_" + kotlin.random.Random.nextLong().toString()
}