package com.kelly3d.arcwave2026.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelly3d.arcwave2026.R

@Composable
actual fun appIconPainter(icon: AppIcon): Painter = when (icon) {
    AppIcon.OpenCloseArrow -> painterResource(id = R.drawable.open_close_arrow)
    AppIcon.Play -> painterResource(id = R.drawable.play)
    AppIcon.Pause -> painterResource(id = R.drawable.pause)
    AppIcon.Next -> painterResource(id = R.drawable.skip)
    AppIcon.Prev -> painterResource(id = R.drawable.skip)
    AppIcon.Shuffle -> painterResource(id = R.drawable.shuffle)
    AppIcon.Repeat -> painterResource(id = R.drawable.repeat)
}

actual val iconSize: Dp = 30.dp
