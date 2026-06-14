package com.kelly3d.arcwave2026.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arcwave.kelly3d.generated.resources.Res
import arcwave.kelly3d.generated.resources.ic_top_row_playlists
import arcwave.kelly3d.generated.resources.ic_top_row_reload
import arcwave.kelly3d.generated.resources.ic_top_row_save_queue
import arcwave.kelly3d.generated.resources.open_close_arrow
import arcwave.kelly3d.generated.resources.pause
import arcwave.kelly3d.generated.resources.play
import arcwave.kelly3d.generated.resources.red_button_housing
import arcwave.kelly3d.generated.resources.red_button_housing_pressed
import arcwave.kelly3d.generated.resources.repeat
import arcwave.kelly3d.generated.resources.shuffle
import arcwave.kelly3d.generated.resources.skip
import arcwave.kelly3d.generated.resources.skip_seconds
import arcwave.kelly3d.generated.resources.white_button_housing
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun appIconPainter(icon: AppIcon): Painter {
    val res = when (icon) {
        AppIcon.OpenCloseArrow -> Res.drawable.open_close_arrow
        AppIcon.Play -> Res.drawable.play
        AppIcon.Pause -> Res.drawable.pause
        AppIcon.Next -> Res.drawable.skip
        AppIcon.Prev -> Res.drawable.skip

        AppIcon.SeekBack -> Res.drawable.skip_seconds
        AppIcon.SeekForward -> Res.drawable.skip_seconds

        AppIcon.Shuffle -> Res.drawable.shuffle
        AppIcon.Repeat -> Res.drawable.repeat

        AppIcon.TopRowReload -> Res.drawable.ic_top_row_reload
        AppIcon.TopRowPlaylists -> Res.drawable.ic_top_row_playlists
        AppIcon.TopRowSaveQueue -> Res.drawable.ic_top_row_save_queue

        AppIcon.RedButtonHousing -> Res.drawable.red_button_housing
        AppIcon.RedButtonHousingPressed -> Res.drawable.red_button_housing_pressed
        AppIcon.WhiteButtonHousing -> Res.drawable.white_button_housing
    }
    return painterResource(res)
}

actual val iconSize: Dp = 30.dp
