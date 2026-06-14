package com.kelly3d.arcwave2026

interface Platform {
    val name: String
}

val Platform.isIos: Boolean get() = name.contains("iOS", ignoreCase = true)

expect fun getPlatform(): Platform