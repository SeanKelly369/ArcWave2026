package com.example.sharpwave2026.player

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidMediaPlayer(
    private val context: Context
) : Player {

    private var mp: MediaPlayer? = null
    private var queue: List<Track> = emptyList()
    private var index: Int = -1

    private val _state = MutableStateFlow(PlayerState())
    override val state: Flow<PlayerState> = _state.asStateFlow()

    override fun setQueue(tracks: List<Track>, startIndex: Int) {
        queue = tracks
        index = startIndex.coerceIn(tracks.indices)
        _state.value = PlayerState(queue = queue, index = index, isPlaying = false)
        prepareCurrent(autoplay = false)
    }

    override fun play() {
        if (queue.isEmpty() || index !in queue.indices) return
        if (mp == null) {
            prepareCurrent(autoplay = true)
            return
        }
        mp?.start()
        _state.value = _state.value.copy(isPlaying = true)
    }

    override fun pause() {
        mp?.pause()
        _state.value = _state.value.copy(isPlaying = false)
    }

    override fun toggle() {
        if (_state.value.isPlaying) pause() else play()
    }

    override fun next() {
        if (queue.isEmpty()) return
        if (index < queue.lastIndex) index++
        _state.value = _state.value.copy(index = index)
        prepareCurrent(autoplay = true)
    }

    override fun prev() {
        if (queue.isEmpty()) return
        if (index > 0) index--
        _state.value = _state.value.copy(index = index)
        prepareCurrent(autoplay = true)
    }

    private fun prepareCurrent(autoplay: Boolean) {
        release()

        val track = queue.getOrNull(index) ?: return
        val rawName = track.uri.ifBlank { track.id }
        val resId = context.resources.getIdentifier(rawName, "raw", context.packageName)
        require(resId != 0) { "Raw resource not found: res/raw/$rawName(.mp3)" }

        mp = MediaPlayer.create(context, resId).apply {
            setOnCompletionListener {
                if (index < queue.lastIndex) {
                    index++
                    _state.value = _state.value.copy(index = index, isPlaying = true)
                    prepareCurrent(autoplay = true)
                } else {
                    _state.value = _state.value.copy(isPlaying = false)
                }
            }
        }

        if (autoplay) {
            mp?.start()
            _state.value = _state.value.copy(isPlaying = true)
        }
    }

    private fun release() {
        mp?.release()
        mp = null
    }
}
