package com.example.sharpwave2026.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.sharpwave2026.R

@Composable
actual fun appIconPainter(icon: AppIcon): Painter {
    val resId = when (icon) {
        AppIcon.OpenCloseArrow -> R.drawable.open_close_arrow
        AppIcon.Play -> R.drawable.play
        AppIcon.Pause -> R.drawable.pause
        AppIcon.Next -> R.drawable.skip
        AppIcon.Prev -> R.drawable.skip
        AppIcon.Shuffle -> R.drawable.shuffle
        AppIcon.Repeat -> R.drawable.repeat
    }
    return painterResource(resId)
}