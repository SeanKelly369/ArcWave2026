package com.example.arcwave2026.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SaveQueueDialog(
    show: Boolean,
    playlistName: String,
    onPlaylistNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmSave: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirmSave) { Text("Save")}
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Save queue as playlist") },
        text = {
            OutlinedTextField(
                value = playlistName,
                onValueChange = onPlaylistNameChange,
                label = { Text("Playlist name") },
                singleLine = true
            )
        }
    )
}