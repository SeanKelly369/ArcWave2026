package com.kelly3d.arcwave2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kelly3d.arcwave2026.ui.theme.ArcWaveTheme
import com.kelly3d.arcwave2026.ui.ArcWaveApp
import com.kelly3d.arcwave2026.player.AndroidContextProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContextProvider.init(this)

        setContent {
            ArcWaveTheme {
                ArcWaveApp()
            }
        }
    }
}