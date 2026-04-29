package com.kelly3d.arcwave2026.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.ui.AppIcon
import com.kelly3d.arcwave2026.ui.appIconPainter
import com.kelly3d.arcwave2026.ui.theme.ArcPlayPauseColours
import com.kelly3d.arcwave2026.ui.theme.arcPlayPauseColours

@Composable
fun ArcPlayPauseButton(
    isPlaying: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 76.dp,
    height: Dp = 46.dp,
    iconSize: Dp = 40.dp
) {
    val colours = arcPlayPauseColours()
    val shape = RoundedCornerShape(999.dp)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "arcPlayPauseScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 8.dp,
        label = "arcPlayPauseShadow"
    )

    val rimPadding by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        label = "arcPlayPauseRim"
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .backgroundRim(
                colours = colours,
                isPressed = isPressed
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(rimPadding)
            .alpha(if (enabled) 1f else 0.55f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .backgroundBody(
                    colours = colours,
                    isPressed = isPressed
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = if (isPressed) 0.12f else 0.24f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.34f)
                    .align(Alignment.TopCenter)
                    .clip(
                        RoundedCornerShape(
                            topStart = 999.dp,
                            topEnd = 999.dp,
                            bottomStart = 36.dp,
                            bottomEnd = 36.dp
                        )
                    )
                    .backgroundGloss(isPressed)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundBottomShade(isPressed)
            )

            Icon(
                painter = appIconPainter(if (isPlaying) AppIcon.Pause else AppIcon.Play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = colours.icon,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

private fun Modifier.backgroundRim(
    colours: ArcPlayPauseColours,
    isPressed: Boolean
): Modifier {
    return background(
        brush = Brush.verticalGradient(
            colors = if (isPressed) {
                listOf(
                    colours.rimTopPressed,
                    colours.rimBottomPressed
                )
            } else {
                listOf(
                    colours.rimTop,
                    colours.rimBottom
                )
            }
        )
    )
}

private fun Modifier.backgroundBody(
    colours: ArcPlayPauseColours,
    isPressed: Boolean
): Modifier {
    return background(
        brush = Brush.verticalGradient(
            colorStops = if (isPressed) {
                arrayOf(
                    0.00f to colours.bodyPressed1,
                    0.08f to colours.bodyPressed2,
                    0.22f to colours.bodyPressed3,

                )
            } else {
                arrayOf(
                    0.00f to colours.body1,
                    0.08f to colours.body2,
                    0.22f to colours.body3,
                    0.41f to colours.body4,
                    0.81f to colours.body5,
                    0.92f to colours.body6,
                    1f to colours.body7
                )
            }
        )
    )
}

private fun Modifier.backgroundGloss(isPressed: Boolean): Modifier {
    return background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isPressed) 0.12f else 0.28f),
                Color.White.copy(alpha = if (isPressed) 0.05f else 0.12f),
                Color.Transparent
            )
        )
    )
}

private fun Modifier.backgroundBottomShade(isPressed: Boolean): Modifier {
    return background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Black.copy(alpha = if (isPressed) 0.32f else 0.18f),
                Color.Black.copy(alpha = if (isPressed) 0.42f else 0.28f)
            )
        )
    )
}