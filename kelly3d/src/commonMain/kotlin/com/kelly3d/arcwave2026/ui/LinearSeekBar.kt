package com.kelly3d.arcwave2026.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import arcwave.kelly3d.generated.resources.Res
import arcwave.kelly3d.generated.resources.seekbar_toggle
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    currentTime: String,
    totalTime: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(40.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            thumb = {
                Icon(
                    painter = painterResource(Res.drawable.seekbar_toggle),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            },
            track = { _ ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                ) {
                    val trackHeight = 10.dp.toPx()
                    val width = size.width
                    val fraction = value
                    val progressWidth = width * fraction
                    val centerY = size.height / 2

                    // Inactive track
                    drawLine(
                        color = Color(0xFFB43C2E),
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

                    // Active track with gradient
                    if (progressWidth > 0f) {
                        val brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4567F6), // Blue
                                Color(0xFFF14A39)  // Red
                            ),
                            start = Offset(0f, centerY),
                            end = Offset(progressWidth, centerY)
                        )

                        drawLine(
                            brush = brush,
                            start = Offset(0f, centerY),
                            end = Offset(progressWidth, centerY),
                            strokeWidth = trackHeight,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        )

        Text(
            text = totalTime,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}
