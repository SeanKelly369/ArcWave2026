package com.kelly3d.arcwave2026.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

data class ArcPlayPauseColours (
    val rimTop: Color,
    val rimBottom: Color,
    val rimTopPressed: Color,
    val rimBottomPressed: Color,

    val bodyTop: Color,
    val bodyMiddle: Color,
    val bodyBottom: Color,

    val bodyTopPressed: Color,
    val bodyMiddlePressed: Color,
    val bodyBottomPressed: Color,

    val icon: Color
)

@Composable
fun arcPlayPauseColours(): ArcPlayPauseColours {
    val scheme = MaterialTheme.colorScheme

    return ArcPlayPauseColours(
        rimTop = Color(0xFFE28F88),
        rimBottom = Color(0xFFD67870),
        rimTopPressed = Color(0xFFD57B72),
        rimBottomPressed = Color(0xFFC86158),

        bodyTop = Color(0xFFD84B2B),
        bodyMiddle = scheme.primary,
        bodyBottom = Color(0xFF8C2115),

        bodyTopPressed = Color(0xFFB52A18),
        bodyMiddlePressed = Color(0xFF951E13),
        bodyBottomPressed = Color(0xFF74180F),

        icon = scheme.onPrimary
    )
}