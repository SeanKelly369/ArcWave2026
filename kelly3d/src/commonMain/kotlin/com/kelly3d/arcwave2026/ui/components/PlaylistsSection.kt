package com.kelly3d.arcwave2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.library.PlayList

@Composable
fun PlaylistsSection(
    playlists: List<PlayList>,
    onPlaylistClick: (PlayList) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        "Playlists",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
    Spacer(Modifier.height(8.dp))

    if (playlists.isEmpty()) {
        Text("No playlists yet.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF444444))
        return
    }

    playlists.forEach { pl ->
        ListItem(
            headlineContent = { Text(pl.name) },
            supportingContent = { Text("${pl.trackUris.size} tracks") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlaylistClick(pl) },
            trailingContent = {
                TextButton(onClick = { onDelete(pl.id) }) { Text("Delete") }
            }
        )
        HorizontalDivider()
    }

}