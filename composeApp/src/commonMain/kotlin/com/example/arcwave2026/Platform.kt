package com.example.arcwave2026

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform