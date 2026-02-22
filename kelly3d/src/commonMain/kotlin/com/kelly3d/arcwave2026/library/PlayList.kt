package com.kelly3d.arcwave2026.library

data class PlayList(
    val id: String,
    val name: String,
    val trackUris: List<String>,
    val kind: Kind = Kind.Manual
) {
    enum class Kind { Auto, Manual }
}
