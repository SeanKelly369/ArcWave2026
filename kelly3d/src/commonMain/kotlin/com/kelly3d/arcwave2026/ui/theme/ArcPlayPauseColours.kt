package com.kelly3d.arcwave2026.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

data class ArcPlayPauseColours (
    val rimTop: Color,
    val rimBottom: Color,
    val rimTopPressed: Color,
    val rimBottomPressed: Color,

    val body1: Color,
    val body2: Color,
    val body3: Color,
    val body4: Color,
    val body5: Color,
    val body6: Color,
    val body7: Color,

    val bodyPressed1: Color,
    val bodyPressed2: Color,
    val bodyPressed3: Color,
    val bodyPressed4: Color,
    val bodyPressed5: Color,
    val bodyPressed6: Color,
    val bodyPressed7: Color,

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

        body1 = Color(0xFFA55C56),
        body2 = Color(0xFFCC695C),
        body3 = Color(0xFFB8483C),
        body4 = Color(0xFFC1240F),
        body5 = Color(0xFF90241A),
        body6 = Color(0xFF761E18),
        body7 = Color(0xFFBF3A30),

        bodyPressed1 = Color(0xFFC76F68),
        bodyPressed2 = Color(0xFFF38071),
        bodyPressed3 = Color(0xFFE05A4C),
        bodyPressed4 = Color(0xFFE73119),
        bodyPressed5 = Color(0xFFB43023),
        bodyPressed6 = Color(0xFFA32922),
        bodyPressed7 = Color(0xFFE34C40),

        icon = scheme.onPrimary
    )
}