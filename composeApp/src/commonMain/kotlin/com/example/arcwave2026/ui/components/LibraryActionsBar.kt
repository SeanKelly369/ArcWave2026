package com.example.arcwave2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LibraryActionsBar(
    isLoading: Boolean,
    tracksCount: Int,
    canSave: Boolean,
    onReload: () -> Unit,
    onSaveQueue: () -> Unit,
    onJumpToPlaylists: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            if (isLoading) "Scanning…" else "Tracks: $tracksCount",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onReload) { Text("Reload") }
            OutlinedButton(onClick = onSaveQueue, enabled = canSave) { Text("Save queue") }
            OutlinedButton(onClick = onJumpToPlaylists) { Text("Playlists") }
        }
    }
}