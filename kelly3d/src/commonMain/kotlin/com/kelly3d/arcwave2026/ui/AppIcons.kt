package com.kelly3d.arcwave2026.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp

enum class AppIcon {
    OpenCloseArrow,
    Play,
    Pause,
    Next,
    Prev,
    Shuffle,
    Repeat
}

@Composable
expect fun appIconPainter(icon: AppIcon): Painter

expect val iconSize: Dp
