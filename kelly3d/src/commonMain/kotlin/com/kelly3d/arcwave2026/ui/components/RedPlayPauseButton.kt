package com.kelly3d.arcwave2026.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.ui.AppIcon
import com.kelly3d.arcwave2026.ui.appIconPainter
import com.kelly3d.arcwave2026.getPlatform
import com.kelly3d.arcwave2026.isIos
import kotlinx.coroutines.delay

@Composable
fun RedPlayPauseButton (
    isPlaying: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 78.dp,
    iconSize: Dp = if (getPlatform().isIos) 24.dp else 40.dp,
    hitAreaSize: Dp = 78.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var visualPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            visualPressed = true
        } else {
            delay(90)
            visualPressed = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "redButtonScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = appIconPainter(
                    if (isPressed) AppIcon.RedButtonHousingPressed else AppIcon.RedButtonHousing
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Icon(
                painter = appIconPainter(if (isPlaying) AppIcon.Pause else AppIcon.Play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }
        }

}