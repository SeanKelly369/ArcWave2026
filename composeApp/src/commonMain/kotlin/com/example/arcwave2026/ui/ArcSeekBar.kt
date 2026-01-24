package com.kelly3d.arcwave2026.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ArcSeekBar(
    positionMs: Long,
    durationMs: Long,
    modifier: Modifier = Modifier,
    diameter: Dp = 240.dp,
    startAngleDeg: Float = 210f,
    sweepAngleDeg: Float = 300f,
    strokeWidth: Float = 18f,
    onScrubStart: () -> Unit = {},
    onScrub: (newPositionMs: Long) -> Unit,
    onScrubEnd: (finalPositionMs: Long) -> Unit
) {
    val dur = durationMs.coerceAtLeast(1L)
    val frac = (positionMs.coerceIn(0L, dur).toFloat() / dur.toFloat()).coerceIn(0f, 1f)

    fun radToDeg(r: Float): Float = r * (180f / PI.toFloat())
    fun degToRad(d: Float): Float = d * (PI.toFloat() / 180f)

    // remember last scrubbed value so drag end returns the right ms
    var lastScrubMs by remember { mutableStateOf(positionMs.coerceIn(0L, dur)) }
    LaunchedEffect(positionMs, dur) {
        // keep it in sync when not dragging externally
        lastScrubMs = positionMs.coerceIn(0L, dur)
    }

    fun pointToFrac(center: Offset, p: Offset): Float {
        val dx = p.x - center.x
        val dy = p.y - center.y

        var ang = radToDeg(atan2(dy, dx))
        ang = (ang + 360f) % 360f

        val start = (startAngleDeg + 360f) % 360f
        val end = (start + sweepAngleDeg) % 360f

        val inArc = if (sweepAngleDeg >= 360f) {
            true
        } else if (start <= end) {
            ang in start..end
        } else {
            ang >= start || ang <= end // wrapped
        }

        fun circDist(a: Float, b: Float): Float {
            val d = abs(a - b) % 360f
            return min(d, 360f - d)
        }

        val clampedAng = if (!inArc) {
            if (circDist(ang, start) < circDist(ang, end)) start else end
        } else ang

        // IMPORTANT: subtract normalized start and fix negative modulo
        var delta = (clampedAng - start + 360f) % 360f
        delta = delta.coerceIn(0f, sweepAngleDeg)

        return (delta / sweepAngleDeg).coerceIn(0f, 1f)
    }

    Canvas(
        modifier = modifier
            .size(diameter)
            .pointerInput(durationMs) {
                detectDragGestures(
                    onDragStart = { onScrubStart() },
                    onDragEnd = { onScrubEnd(lastScrubMs.coerceIn(0L, dur)) },
                    onDragCancel = { onScrubEnd(lastScrubMs.coerceIn(0L, dur)) },
                    onDrag = { change, _ ->
                        val center = Offset(diameter.toPx() / 2f, diameter.toPx() / 2f)
                        val f = pointToFrac(center, change.position)
                        val ms = (f * dur).toLong().coerceIn(0L, dur)
                        lastScrubMs = ms
                        onScrub(ms)
                    }
                )
            }
    ) {
        val w = min(size.width, size.height)
        val pad = strokeWidth / 2f + 6f
        val arcSize = Size(w - pad * 2f, w - pad * 2f)
        val topLeft = Offset(pad, pad)

        // background arc
        drawArc(
            color = Color(0x22000000),
            startAngle = startAngleDeg,
            sweepAngle = sweepAngleDeg,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // progress arc
        drawArc(
            color = Color(0xFFE0A04B),
            startAngle = startAngleDeg,
            sweepAngle = sweepAngleDeg * frac,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // thumb position
        val angRad = degToRad(startAngleDeg + sweepAngleDeg * frac)
        val radius = (w - pad * 2f) / 2f
        val cx = w / 2f
        val cy = w / 2f
        val tx = cx + cos(angRad) * radius
        val ty = cy + sin(angRad) * radius

        drawCircle(
            color = Color(0xFFE0A04B),
            radius = strokeWidth * 0.55f,   // FIXED (was /)
            center = Offset(tx, ty)
        )
        drawCircle(
            color = Color.White,
            radius = strokeWidth * 0.25f,
            center = Offset(tx, ty)
        )
    }
}
