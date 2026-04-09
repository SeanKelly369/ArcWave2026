package com.kelly3d.arcwave2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            if (isLoading) "Scanning…" else "Tracks: $tracksCount",
            style = MaterialTheme.typography.bodyMedium
        )

    }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        LibraryActionButtonsRow(
            canSave = canSave,
            onReload = onReload,
            onSaveQueue = onSaveQueue,
            onJumpToPlaylists = onJumpToPlaylists
        )
    }
}