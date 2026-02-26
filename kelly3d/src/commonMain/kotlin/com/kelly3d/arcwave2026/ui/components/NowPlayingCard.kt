package com.kelly3d.arcwave2026.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.player.PlayerState
import com.kelly3d.arcwave2026.ui.AppIcon
import com.kelly3d.arcwave2026.ui.appIconPainter
import com.kelly3d.arcwave2026.utils.formatMs
import com.kelly3d.arcwave2026.ui.ArcSeekBar

@Composable
fun NowPlayingCard (
    state: PlayerState,
    durationMs: Long,
    dragMs: Long,
    onDragMsChange: (Long) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    showArcToggle: Boolean,
    arcMenuOpen: Boolean,
    onArcMenuToggle: () -> Unit,
    onPrev: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
    arcDiameter: Dp = 260.dp
) {
    Card(
        modifier.padding(12.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
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
                    onClick = onPrev,
                    enabled = state.queue.isNotEmpty(),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { Text("Prev") }

                Button(
                    onClick = onToggle,
                    enabled = state.queue.isNotEmpty(),
                    modifier = Modifier.align ( Alignment.Center ),
                ) { Text(if (state.isPlaying) "Pause" else "Play") }

                OutlinedButton(
                    onClick = onNext,
                    enabled = state.queue.isNotEmpty(),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { Text("Next") }
            }

            var isLinearScrubbing by remember { mutableStateOf(false) }
            var linearDragMs by remember { mutableStateOf(dragMs) }

            LaunchedEffect(dragMs, isLinearScrubbing) {
                if (!isLinearScrubbing) linearDragMs = dragMs
            }

            Slider(
                value = (linearDragMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f),
                onValueChange = { frac ->
                    isLinearScrubbing = true
                    onDraggingChange(true)

                    val ms = (frac * durationMs).toLong().coerceIn(0L, durationMs)
                    linearDragMs = ms
                    onDragMsChange((frac * durationMs).toLong())
                },
                onValueChangeFinished = {
                    // use the local value, not the (possibly stale) parameter
                    val ms = linearDragMs.coerceIn(0L, durationMs)
                    isLinearScrubbing = false
                    onDraggingChange(false)
                    onSeekTo(ms)
                },
                enabled = state.queue.isNotEmpty() && durationMs > 1L,
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
                    formatMs(durationMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF444444)
                )
            }

            Spacer(Modifier.height(6.dp))

            if (showArcToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onArcMenuToggle) {
                        Icon(
                            painter = appIconPainter(AppIcon.OpenCloseArrow),
                            contentDescription = if (arcMenuOpen) "Close arc menu" else "Open arc menu",
                            modifier = Modifier.rotate(if (arcMenuOpen) 180f else 0f)
                        )
                    }
                }

                AnimatedVisibility(visible = arcMenuOpen) {
                    val windowMs = 8L * 60L * 1000L

                    var isArcScrubbing by remember { mutableStateOf(false) }
                    var frozenWindowStart by remember { mutableStateOf(0L) }
                    var frozenWindowEnd by remember { mutableStateOf(0L) }
                    var scrubBaseStart by remember { mutableStateOf(0L) }

                    val displayMs = if (isLinearScrubbing) linearDragMs else dragMs

                    val (autoStart, autoEnd) = computeSeekWindow(displayMs, durationMs, windowMs)

                    val startAbs = if (isArcScrubbing) frozenWindowStart else autoStart
                    val endAbs = if (isArcScrubbing) frozenWindowEnd else autoEnd

                    val windowLen = (endAbs - startAbs).coerceAtLeast(1L)
                    val localPos = (displayMs - startAbs).coerceIn(0L, windowLen)

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ArcSeekBar(
                            positionMs = localPos,
                            durationMs = windowLen,
                            diameter = arcDiameter,
                            onScrubStart = {
                                isArcScrubbing = true
                                onDraggingChange(true)

                                val (s, e) = computeSeekWindow(dragMs, durationMs, windowMs)
                                frozenWindowStart = s
                                frozenWindowEnd = e
                                scrubBaseStart = s
                            },
                            onScrub = { localMs ->
                                val absolute = (scrubBaseStart + localMs).coerceIn(0L, durationMs)
                                onDragMsChange(absolute)
                            },
                            onScrubEnd = { localMs ->
                                val absolute = (scrubBaseStart + localMs).coerceIn(0L, durationMs)

                                onDragMsChange(absolute)
                                onSeekTo(absolute)
                                isArcScrubbing = false
                                onDraggingChange(false)
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            formatMs(dragMs),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun computeSeekWindow(
    anchorMs: Long,
    durationMs: Long,
    windowMs: Long
): Pair<Long, Long> {
    if(durationMs <= windowMs) return 0L to durationMs

    val half = windowMs / 2
    var start = anchorMs - half
    var end = anchorMs + half

    if (start < 0L) {
        start = 0L
        end = windowMs
    }

    if (end > durationMs) {
        end = durationMs
        start = durationMs - windowMs
    }
    return start to end
}
