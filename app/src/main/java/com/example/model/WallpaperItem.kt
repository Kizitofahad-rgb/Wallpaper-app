package com.example.model

import androidx.annotation.DrawableRes

enum class WallpaperCategory(val title: String) {
    ALL("All Multiverse"),
    MONARCH("Shadow & Dark"),
    CYBERPUNK("Cyber Samurai"),
    VALKYRIE("Flame & Divinity"),
    TITAN("Cosmic Titans")
}

data class WallpaperItem(
    val id: String,
    val title: String,
    val seriesBadge: String,
    val category: WallpaperCategory,
    @DrawableRes val resId: Int,
    val resolution: String = "3840 x 2160 (4K UHD)",
    val depthRating: String = "9.9/10 Hyper-3D",
    val particleEffect: ParticleType = ParticleType.EMBER,
    val accentColorHex: Long = 0xFF00F0FF,
    val description: String,
    val depthLayers: Int = 5,
    val fps: Int = 120
)

enum class ParticleType {
    EMBER,
    NEON_SPARKS,
    COSMIC_RUNES,
    VOID_MIST
}
