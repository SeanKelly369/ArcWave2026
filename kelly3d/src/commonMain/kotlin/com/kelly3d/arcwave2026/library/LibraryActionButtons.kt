package com.kelly3d.arcwave2026.library

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.ui.AppIcon
import com.kelly3d.arcwave2026.ui.appIconPainter

@Composable
fun LibraryActionButtonsRow(
    canSave: Boolean,
    onReload: () -> Unit,
    onSaveQueue: () -> Unit,
    onJumpToPlaylists: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryActionButton(
            text = "Reload",
            icon = AppIcon.TopRowReload,
            onClick = onReload
        )

        LibraryActionButton(
            text = "Save Queue",
            icon = AppIcon.TopRowSaveQueue,
            onClick = onSaveQueue,
            enabled = canSave
        )

        LibraryActionButton(
            text = "Playlists",
            icon = AppIcon.TopRowPlaylists,
            onClick = onJumpToPlaylists
        )
    }
}

@Composable
private fun LibraryActionButton(
    text: String,
    icon: AppIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColour by animateColorAsState(
        targetValue = when {
            !enabled -> Color(0xFFF4F4F6)
            isPressed -> Color(0xFFECE8F1)
            else -> Color(0xFFF9F8FB)
        },
        label = "libraryActionBackground"
    )

    val borderColour by animateColorAsState(
        targetValue = when {
            !enabled -> Color(0xFFD8D6DC)
            isPressed -> Color(0xFFC8C2D1)
            else -> Color(0xFFD9D4E0)
        },
        label = "libraryActionBorder"
    )

    val contentColour by animateColorAsState(
        targetValue = when {
            !enabled -> Color(0xFFAAA7B0)
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "libraryActionContent"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        label = "libraryActionScale"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 0.dp,
        label = "libraryActionOffsetY"
    )

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColour),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColour,
            contentColor = contentColour,
            disabledContainerColor = Color(0xFFF4F4F6),
            disabledContentColor = Color(0xFFAAA7B0)
        ),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .defaultMinSize(minHeight = 42.dp)
            .scale(scale)
            .offset(y = offsetY)
        ) {
        Icon(
            painter = appIconPainter(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Unspecified
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}