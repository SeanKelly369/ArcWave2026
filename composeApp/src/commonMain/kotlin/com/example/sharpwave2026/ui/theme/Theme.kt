package com.example.sharpwave2026.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Emerald = Color(0xFF053833)
private val Copper = Color(0xFFA16B2A)
private val Gold = Color(0xFFDAB331)

private val Paper = Color(0xFFFAFAF7)
private val DarkBg = Color(0xFF0A100F)
private val DarkSurface = Color(0xFF0A0E0C)

private val LightScheme = lightColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    secondary = Copper,
    onSecondary = Color(0xFF111311),
    tertiary = Gold,
    background = Paper,
    onBackground = Color(0xFF111311),
    surface = Color.White,
    onSurface = Color(0xFF111311),
    surfaceVariant = Color(0xFFEDEBE6),
    onSurfaceVariant = Color(0xFF2B2F2B),
    outline = Color(0xFFCED4CF)
)

private val DarkScheme = darkColorScheme(
    primary = Emerald,
    onPrimary = Color(0xFFE9EEE8),
    secondary = Copper,
    onSecondary = Color(0xFF111311),
    tertiary = Gold,
    background = DarkBg,
    onBackground = Color(0xFFE9EEE8),
    surface = DarkSurface,
    onSurface = Color(0xFFE9EEE8),
    surfaceVariant = Color(0xFF1A2620),
    onSurfaceVariant = Color(0xFFCBD6CF),
    outline = Color(0xFF2F3E36)
)

@Composable
fun ArcWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme
    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}