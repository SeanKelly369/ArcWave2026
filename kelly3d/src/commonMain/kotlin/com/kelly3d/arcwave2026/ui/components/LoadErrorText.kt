package com.kelly3d.arcwave2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadErrorText(loadError: String?) {
    loadError?.let {
        Spacer(Modifier.height(6.dp))
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF444444)
        )
    }
}