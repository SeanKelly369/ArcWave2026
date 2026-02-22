package com.kelly3d.arcwave2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.player.Track

@Composable
fun QueueSection(
    queue: List<Track>,
    currentIndex: Int,
    isPlaying: Boolean,
    onTapTrack: (Int) -> Unit
) {
    Text("Queue", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    queue.forEachIndexed { idx, t ->
        ListItem(
            headlineContent = { Text(t.title) },
            supportingContent = { Text(t.artist) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTapTrack(idx) },
            trailingContent = {
                if (idx == currentIndex) Text(if (isPlaying) "▶" else "Ⅱ")
            }
        )
        HorizontalDivider()
    }

}