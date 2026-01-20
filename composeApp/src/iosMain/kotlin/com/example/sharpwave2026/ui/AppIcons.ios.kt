package com.example.sharpwave2026.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import sharpwave2026.composeapp.generated.resources.Res
import sharpwave2026.composeapp.generated.resources.open_close_arrow
import sharpwave2026.composeapp.generated.resources.pause
import sharpwave2026.composeapp.generated.resources.play
import sharpwave2026.composeapp.generated.resources.repeat
import sharpwave2026.composeapp.generated.resources.shuffle
import sharpwave2026.composeapp.generated.resources.skip

@Composable
actual fun appIconPainter(icon: AppIcon): Painter {
    val res = when (icon) {
        AppIcon.OpenCloseArrow -> Res.drawable.open_close_arrow
        AppIcon.Play -> Res.drawable.play
        AppIcon.Pause -> Res.drawable.pause
        AppIcon.Next -> Res.drawable.skip
        AppIcon.Prev -> Res.drawable.skip
        AppIcon.Shuffle -> Res.drawable.shuffle
        AppIcon.Repeat -> Res.drawable.repeat
    }
    return painterResource(res)
}