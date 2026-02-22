package com.kelly3d.arcwave2026.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.library.PlayListStore
import com.kelly3d.arcwave2026.library.provideAudioLibrary
import com.kelly3d.arcwave2026.player.PlayerState
import com.kelly3d.arcwave2026.player.Track
import com.kelly3d.arcwave2026.player.providePlayer
import com.kelly3d.arcwave2026.ui.components.*
import com.kelly3d.arcwave2026.utils.logd
import com.kelly3d.arcwave2026.utils.loge
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

@Composable
fun ArcWaveApp() {
    AudioPermissionGate(
        onGranted = { ArcWaveContent() },
        onDenied = {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                HeaderTitle(title = "Audio Arc")
                Spacer(Modifier.height(8.dp))
                LoadErrorText("Storage/media permission is required to scan audio files.")
            }
        }
    )
}

@Composable
private fun ArcWaveContent() {
    var pendingSeekMs by remember { mutableStateOf<Long?>(null) }

    val player = providePlayer()
    val state by player.state.collectAsState(PlayerState())
    val library = provideAudioLibrary()

    val playListStore = remember { PlayListStore() }
    val playlists by playListStore.playlists.collectAsState()

    var allTracks by remember { mutableStateOf<List<Track>>(emptyList()) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPlayListName by remember { mutableStateOf("") }

    var loadError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var reloadToken by remember { mutableStateOf(0) }

    val tag = "arc_wave_app"

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val playlistsBir = remember { BringIntoViewRequester() }

    LaunchedEffect(reloadToken) {
        logd(tag, "Reload triggered. reloadToken=$reloadToken")
        isLoading = true
        loadError = null
        try {
            logd(tag, "Scanning tracks…")
            val tracks = library.scanTracks()
            logd(tag, "Scan complete. tracksFound=${tracks.size}")

            allTracks = tracks

            if (tracks.isNotEmpty()) {
                logd(tag, "Setting queue. startIndex=0 firstUri=${tracks.firstOrNull()?.uri}")
                player.setQueue(tracks, startIndex = 0)
            } else {
                loadError = "No audio files found (or permission not granted yet)."
                logd(tag, "No tracks found. loadError set.")
            }
        } catch (t: Throwable) {
            loadError = t.message ?: t.toString()
            loge(tag, "scanTracks failed: $loadError", t)
        } finally {
            isLoading = false
            logd(tag, "Loading finished. isLoading=false loadError=${loadError != null}")
        }
    }

    val dur = state.durationMs.coerceAtLeast(1L)
    val pos = state.positionMs.coerceIn(0L, dur)

    // 20 minutes in ms
    val showArcToggle = dur >= 20L * 60L * 1000L && state.queue.isNotEmpty() && state.current != null

    var arcMenuOpen by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var dragMs by remember { mutableStateOf(0L) }

    LaunchedEffect(state.current?.uri) {
        arcMenuOpen = false
        val cur = state.current
        logd(tag, "Track change -> arcMenuOpen=false; uri=${cur?.uri} title=${cur?.title} index=${state.index}/${state.queue.size}")
    }

    LaunchedEffect(pos, dur, isDragging, pendingSeekMs) {
        if (!isDragging) {
            val pending = pendingSeekMs
            if (pending == null) {
                dragMs = pos
            } else {
                if (kotlin.math.abs(pos - pending) <= 750L) {
                    pendingSeekMs = null
                    dragMs = pos
                } else {
                    dragMs = pending.coerceIn(0L, dur)
                }
            }
        }
    }

    fun resolveTracksByUri(trackUris: List<String>, all: List<Track>): List<Track> {
        val map = all.associateBy { it.uri }
        return trackUris.mapNotNull { map[it] }
    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        HeaderTitle(title = "Audio Arc")
        Spacer(Modifier.height(8.dp))

        LibraryActionsBar(
            isLoading = isLoading,
            tracksCount = state.queue.size,
            canSave = state.queue.isNotEmpty(),
            onReload = { reloadToken++ },
            onSaveQueue = { showSaveDialog = true },
            onJumpToPlaylists = { scope.launch { playlistsBir.bringIntoView() } }
        )

        LoadErrorText(loadError)

        SaveQueueDialog(
            show = showSaveDialog,
            playlistName = newPlayListName,
            onPlaylistNameChange = { newPlayListName = it },
            onDismiss = { showSaveDialog = false },
            onConfirmSave = {
                val uris = state.queue.mapNotNull { t -> t.uri.takeIf { it.isNotBlank() } }
                if (uris.isNotEmpty()) {
                    playListStore.saveQueueAsPlaylist(
                        name = newPlayListName.ifBlank { "Queue" },
                        queue = uris
                    )
                }
                newPlayListName = ""
                showSaveDialog = false
            }
        )

        Spacer(Modifier.height(12.dp))

        NowPlayingCard(
            state = state,
            durationMs = dur,
            positionMs = pos,
            dragMs = dragMs,
            onDragMsChange = { dragMs = it },
            onDraggingChange = { isDragging = it },
            showArcToggle = showArcToggle,
            arcMenuOpen = arcMenuOpen,
            onArcMenuToggle = {
                arcMenuOpen = !arcMenuOpen
                logd(tag, "Arc menu toggled. arcMenuOpen=$arcMenuOpen showArcToggle=$showArcToggle durMs=$dur")
            },
            onPrev = player::prev,
            onToggle = player::toggle,
            onNext = player::next,
            onSeekTo = { ms ->
                val target = ms.coerceIn(0L, dur)
                pendingSeekMs = target
                dragMs = target
                player.seekTo(target)
            }
        )

        Spacer(Modifier.height(16.dp))

        PlaylistsSection(
            modifier = Modifier.bringIntoViewRequester(playlistsBir),
            playlists = playlists,
            onDelete = { id -> playListStore.delete(id) },
            onPlaylistClick = { pl ->
                val tracks = resolveTracksByUri(pl.trackUris, allTracks)
                if (tracks.isNotEmpty()) {
                    player.setQueue(tracks, startIndex = 0)
                    player.play()
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        QueueSection(
            queue = state.queue,
            currentIndex = state.index,
            isPlaying = state.isPlaying,
            onTapTrack = { idx ->
                player.setQueue(state.queue, idx)
                player.play()
            }
        )

    }
}
