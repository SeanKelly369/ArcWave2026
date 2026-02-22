package com.kelly3d.arcwave2026

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform