package com.example.arcwave2026.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.arcwave2026.library.PlayListStore
import com.example.arcwave2026.library.provideAudioLibrary
import com.example.arcwave2026.player.PlayerState
import com.example.arcwave2026.player.Track
import com.example.arcwave2026.player.providePlayer
import com.example.arcwave2026.utils.formatMs
import com.example.arcwave2026.utils.logd
import com.example.arcwave2026.utils.loge
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import com.kelly3d.arcwave2026.ui.ArcSeekBar
import kotlinx.coroutines.launch

@Composable
fun ArcWaveApp() {

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

    LaunchedEffect(state.current?.uri) {
        arcMenuOpen = false
        val cur = state.current
        logd(
            tag,
            "Track change -> arcMenuOpen=false; uri=${cur?.uri} title=${cur?.title} index=${state.index}/${state.queue.size}"
        )
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragMs by remember { mutableStateOf(0L) }

    LaunchedEffect(pos, dur, isDragging) {
        if (!isDragging) dragMs = pos
    }

    fun resolveTracksByUri(trackUris: List<String>, all: List<Track>): List<Track> {
        val map = all.associateBy { it.uri }
        return trackUris.mapNotNull { map[it] }
    }

    MaterialTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            Text(
                text = "Audio Arc",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isLoading) {
                    Text("Scanning…", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Tracks: ${state.queue.size}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { reloadToken++ }) { Text("Reload") }

                    OutlinedButton(
                        onClick = { showSaveDialog = true },
                        enabled = state.queue.isNotEmpty()
                    ) { Text("Save queue") }

                    OutlinedButton(
                        onClick = { scope.launch { playlistsBir.bringIntoView() }}
                    ) { Text("Playlists") }
                }
            }

            loadError?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF444444)
                )
            }

            // Save Queue dialog
            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val uris = state.queue.mapNotNull { t ->
                                    t.uri.takeIf { it.isNotBlank() }
                                }
                                if (uris.isNotEmpty()) {
                                    playListStore.saveQueueAsPlaylist(
                                        name = newPlayListName.ifBlank { "Queue" },
                                        queue = uris
                                    )
                                }
                                newPlayListName = ""
                                showSaveDialog = false
                            }
                        ) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
                    },
                    title = { Text("Save queue as playlist") },
                    text = {
                        OutlinedTextField(
                            value = newPlayListName,
                            onValueChange = { newPlayListName = it },
                            label = { Text("Playlist name") },
                            singleLine = true
                        )
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Now Playing card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = state.current?.title ?: "Nothing selected",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.current?.artist ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = player::prev,
                            enabled = state.queue.isNotEmpty(),
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) { Text("Prev") }

                        Button(
                            onClick = player::toggle,
                            enabled = state.queue.isNotEmpty(),
                            modifier = Modifier.align(Alignment.Center)
                        ) { Text(if (state.isPlaying) "Pause" else "Play") }

                        OutlinedButton(
                            onClick = player::next,
                            enabled = state.queue.isNotEmpty(),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) { Text("Next") }
                    }

                    Slider(
                        value = dragMs.toFloat() / dur.toFloat(),
                        onValueChange = { frac ->
                            isDragging = true
                            dragMs = (frac * dur).toLong()
                        },
                        onValueChangeFinished = {
                            isDragging = false
                            player.seekTo(dragMs)
                        },
                        enabled = state.queue.isNotEmpty() && dur > 1L,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            formatMs(dragMs),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            formatMs(dur),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF444444)
                        )
                    }
                }

                // Arrow
                Spacer(Modifier.height(6.dp))

                if (showArcToggle) {
                   Row (
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.End
                   ) {
                       IconButton(onClick = {
                           arcMenuOpen = !arcMenuOpen
                           logd(tag, "Arc menu toggled. arcMenuOpen=$arcMenuOpen showArcToggle=$showArcToggle durMs=$dur")
                       }) {
                           Icon(
                               painter = appIconPainter(AppIcon.OpenCloseArrow),
                               contentDescription = if (arcMenuOpen) "Close arc menu" else "Open arc menu",
                               modifier = Modifier.rotate(if (arcMenuOpen) 180f else 0f)
                           )
                       }
                   }

                    // Menu area that reveals the arc UI
                    AnimatedVisibility(visible = arcMenuOpen) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ArcSeekBar(
                                positionMs = dragMs,
                                durationMs = dur,
                                diameter = 260.dp,
                                onScrubStart = { isDragging = true },
                                onScrub = { ms -> dragMs = ms },
                                onScrubEnd = { ms ->
                                    isDragging = false
                                    player.seekTo(ms)
                                }
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    formatMs(dragMs),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF444444)
                                )
                            }
                        }
                    }
                }
            }


            Spacer(Modifier.height(16.dp))

            // Playlists
            Text(
                "Playlists",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.bringIntoViewRequester(playlistsBir)
            )
            Spacer(Modifier.height(8.dp))

            if (playlists.isEmpty()) {
                Text("No playlists yet.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF444444))
            } else {
                playlists.forEach { pl ->
                    ListItem(
                        headlineContent = { Text(pl.name) },
                        supportingContent = { Text("${pl.trackUris.size} tracks") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val tracks = resolveTracksByUri(pl.trackUris, allTracks)
                                if (tracks.isNotEmpty()) {
                                    player.setQueue(tracks, startIndex = 0)
                                    player.play()
                                }
                            },
                        trailingContent = {
                            TextButton(onClick = { playListStore.delete(pl.id) }) { Text("Delete") }
                        }
                    )
                    HorizontalDivider()
                }
            }

            Spacer(Modifier.height(16.dp))

            // Queue
            Text("Queue", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            state.queue.forEachIndexed { idx, t ->
                ListItem(
                    headlineContent = { Text(t.title) },
                    supportingContent = { Text(t.artist) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            player.setQueue(state.queue, idx)
                            player.play()
                        },
                    trailingContent = {
                        if (idx == state.index) Text(if (state.isPlaying) "▶" else "Ⅱ")
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
