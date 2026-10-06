package com.example.data

import com.example.R
import com.example.model.ParticleType
import com.example.model.WallpaperCategory
import com.example.model.WallpaperItem
import kotlinx.coroutines.flow.Flow

class WallpaperRepository(private val favoriteDao: FavoriteDao) {

    val catalog: List<WallpaperItem> = listOf(
        WallpaperItem(
            id = "shadow_monarch_01",
            title = "Shadow Monarch: Absolute Sovereign",
            seriesBadge = "ORIGIN 4K",
            category = WallpaperCategory.MONARCH,
            resId = R.drawable.img_anime_solo_monarch_1791263255280,
            resolution = "3840 x 2160 UHD Ultra",
            depthRating = "9.9/10 Dimensional Pop",
            particleEffect = ParticleType.VOID_MIST,
            accentColorHex = 0xFFBD00FF,
            description = "Ethereal dark commander unleashing violet void legions with hyper-volumetric mist bursting forward through the glass display.",
            depthLayers = 5,
            fps = 120
        ),
        WallpaperItem(
            id = "cyber_samurai_02",
            title = "Neo-Tokyo Ronin: Cyber Blade",
            seriesBadge = "CYBERNETIC 4K",
            category = WallpaperCategory.CYBERPUNK,
            resId = R.drawable.img_anime_cyber_samurai_1791263266645,
            resolution = "4096 x 2304 Cinema 4K",
            depthRating = "9.8/10 Extreme Depth",
            particleEffect = ParticleType.NEON_SPARKS,
            accentColorHex = 0xFF00F0FF,
            description = "High-velocity katana slash breaking out from rain-drenched neon streets with holographic sakura and dynamic reflections.",
            depthLayers = 6,
            fps = 120
        ),
        WallpaperItem(
            id = "flame_valkyrie_03",
            title = "Phoenix Ascendant: Solar Valkyrie",
            seriesBadge = "DIVINE 4K",
            category = WallpaperCategory.VALKYRIE,
            resId = R.drawable.img_anime_flame_valkyrie_1791263279606,
            resolution = "3840 x 2160 UHD Ultra",
            depthRating = "10.0/10 3D Out-Of-Screen",
            particleEffect = ParticleType.EMBER,
            accentColorHex = 0xFFFF2A6D,
            description = "Blazing golden & crimson wings expanding in multi-tier parallax with soaring embers bursting outward towards your touch.",
            depthLayers = 5,
            fps = 120
        ),
        WallpaperItem(
            id = "void_titan_04",
            title = "Astral Monarch: Cosmic Hand",
            seriesBadge = "CELESTIAL 4K",
            category = WallpaperCategory.TITAN,
            resId = R.drawable.img_anime_void_titan_1791263290492,
            resolution = "3840 x 2160 UHD Ultra",
            depthRating = "9.9/10 Void Immersion",
            particleEffect = ParticleType.COSMIC_RUNES,
            accentColorHex = 0xFFFFD166,
            description = "Cosmic astral entity reaching its hand through the celestial veil with floating holographic galactic runes in 3D perspective.",
            depthLayers = 5,
            fps = 120
        )
    )

    fun getAllFavorites(): Flow<List<FavoriteWallpaper>> = favoriteDao.getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    suspend fun toggleFavorite(wallpaper: WallpaperItem, currentStatus: Boolean) {
        if (currentStatus) {
            favoriteDao.removeFavorite(wallpaper.id)
        } else {
            favoriteDao.addFavorite(
                FavoriteWallpaper(
                    id = wallpaper.id,
                    title = wallpaper.title
                )
            )
        }
    }
}
