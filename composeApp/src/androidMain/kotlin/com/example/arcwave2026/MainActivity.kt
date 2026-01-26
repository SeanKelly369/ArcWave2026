package com.example.arcwave2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.arcwave2026.ui.ArcWaveApp
import com.example.arcwave2026.ui.theme.ArcWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArcWaveTheme {
                ArcWaveApp()
            }
        }
    }
}
