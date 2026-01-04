package com.example.sharpwave2026.player

import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.darwin.NSObject
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosMediaPlayer: Player {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private var ticker: Job? = null
    private val tickMs = 200L

    private var ap: AVAudioPlayer? = null;
    private var queue: List<Track> = emptyList()
    private var index: Int = -1;

    private val _state = MutableStateFlow(PlayerState())
    override val state: Flow<PlayerState> = _state.asStateFlow()

    private val delegate = object: NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            stopTicker()

            // auto-advance like Android
            if (index < queue.lastIndex) {
                index++
                _state.value = _state.value.copy(index = index, isPlaying = true, positionMs = 0L)
                prepareCurrent(autoplay = true)
            } else {
                _state.value = _state.value.copy(isPlaying = false, positionMs = _state.value.durationMs)
            }
        }
    }

    init {
        NSLog("IosMediaPlayer INIT")
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
    }

    override fun setQueue(tracks: List<Track>, startIndex: Int) {
        queue = tracks
        index = if (tracks.isEmpty()) -1 else startIndex.coerceIn(tracks.indices)

        _state.value = PlayerState(
            queue = queue,
            index = index,
            isPlaying = false,
            positionMs = 0L,
            durationMs = 0L
        )

        prepareCurrent(autoplay = false)
    }

    override fun play() {
        if (queue.isEmpty() || index !in queue.indices) return

        val player = ap
        if (player == null) {
            prepareCurrent(autoplay = true)
            return
        }

        ensureSession()
        player.play()
        _state.value = _state.value.copy(isPlaying = true)
        startTicker()
    }

    override fun pause() {
        ap?.pause()
        _state.value = _state.value.copy(isPlaying = false)
        stopTicker()
    }

    override fun toggle() {
        if (_state.value.isPlaying) pause() else play()
    }

    override fun next() {
        if (queue.isEmpty()) return
        if (index < queue.lastIndex) index++
        _state.value = _state.value.copy(index = index, positionMs = 0L)
        prepareCurrent(autoplay = true)
    }

    override fun prev() {
        if (queue.isEmpty()) return
        if (index > 0) index--
        _state.value = _state.value.copy(index = index, positionMs = 0L)
        prepareCurrent(autoplay = true)
    }

    override fun seekTo(positionMs: Long) {
        val player = ap ?: return

        val durMs = (player.duration * 1000.0).toLong().coerceAtLeast(1L)
        val targetMs = positionMs.coerceIn(0L, durMs)

        player.currentTime = targetMs / 1000.0
        _state.value = _state.value.copy(positionMs = targetMs, durationMs = durMs)

        // if it is playing, keep the slider moving
        if (ap?.playing == true) startTicker()
    }

    private fun prepareCurrent(autoplay: Boolean) {
        release()

        val track = queue.getOrNull(index) ?: return

        val url = resolveTrackUrl(track)
            ?: error("Track URL not found (bundle or file): id=${track.id} uri=${track.uri}")

        memScoped {
            val err = alloc<ObjCObjectVar<NSError?>>()

            val player = AVAudioPlayer(contentsOfURL = url, error = err.ptr)

                val initError = err.value
                require(initError == null) {
                    "AVAudioPlayer failed: ${initError?.localizedDescription ?: "unknown"}"
                }

            player.delegate = delegate
            player.prepareToPlay()
            ap = player

            val durMs = (player.duration * 1000.0).toLong().coerceAtLeast(0L)
            _state.value = _state.value.copy(durationMs = durMs, positionMs = 0L)
        }

        if (autoplay) {
            ap?.play()
            _state.value = _state.value.copy(isPlaying = true)
            startTicker()
        } else {
            _state.value = _state.value.copy(isPlaying = false)
        }
    }

    private fun resolveTrackUrl(track: Track): NSURL? {
        val raw = track.uri.trim()

        if (raw.isNotEmpty()) {
            val direct = NSURL(string = raw)
            if (direct != null) return direct

            val expanded = expandTile(raw)
            if (expanded.startsWith("/")) {
                return NSURL.fileURLWithPath(expanded)
            }

            val noExt = stripExtension(raw)
            NSBundle.mainBundle.URLForResource(noExt, withExtension = "mp3")?.let { return it }
            NSBundle.mainBundle.URLForResource(noExt, withExtension = "m4a")?.let { return it }
            NSBundle.mainBundle.URLForResource(noExt, withExtension = "wav")?.let { return it }
        }

        val idNoExt = stripExtension(track.id)
        NSBundle.mainBundle.URLForResource(idNoExt, withExtension = "mp3")?.let { return it }
        NSBundle.mainBundle.URLForResource(idNoExt, withExtension = "m4a")?.let { return it }
        return NSBundle.mainBundle.URLForResource(idNoExt, withExtension = "wav")
    }

    private fun expandTile(path: String): String {
        return when {
            path == "~" -> NSHomeDirectory()
            path.startsWith("~/") -> NSHomeDirectory() + path.removePrefix("~")
            else -> path
        }
    }

    private fun stripExtension(filename: String): String {
        val dot = filename.lastIndexOf('.')
        return if (dot > 0) filename.substring(0, dot) else filename
    }

    private fun ensureSession() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
    }

    private fun startTicker() {
        if (ticker?.isActive == true) return

        ticker = scope.launch {
            while (isActive) {
                delay(tickMs)

                val player = ap ?: continue
                if (!player.playing) continue

                val posMs = (player.currentTime * 1000.0).toLong()
                val durMs = (player.duration * 1000.0).toLong().coerceAtLeast(1L)

                _state.value = _state.value.copy(
                    positionMs = posMs.coerceAtMost(durMs),
                    durationMs = durMs
                )
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    private fun release() {
        stopTicker()
        ap?.stop()
        ap = null
    }

    fun dispose() {
        release()
        job.cancel()
    }
}