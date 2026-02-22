package com.kelly3d.arcwave2026.ui

import androidx.compose.runtime.Composable

@Composable
expect fun AudioPermissionGate(
    onGranted: @Composable () -> Unit,
    onDenied: @Composable () -> Unit = {}
)