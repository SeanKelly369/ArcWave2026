package com.kelly3d.arcwave2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kelly3d.arcwave2026.library.LibraryActionButtonsRow

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
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            if (isLoading) "Scanning…" else "Tracks: $tracksCount",
            style = MaterialTheme.typography.bodyMedium
        )

        LibraryActionButtonsRow(
            canSave = canSave,
            onReload = onReload,
            onSaveQueue = onSaveQueue,
            onJumpToPlaylists = onJumpToPlaylists
        )
    }
}