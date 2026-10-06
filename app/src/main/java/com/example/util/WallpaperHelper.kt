package com.example.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.DrawableRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class WallpaperTarget {
    HOME,
    LOCK,
    BOTH
}

object WallpaperHelper {

    suspend fun applyWallpaper(
        context: Context,
        @DrawableRes resId: Int,
        target: WallpaperTarget
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                ?: return@withContext Result.failure(Exception("Unable to decode 4K wallpaper bitmap"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val which = when (target) {
                    WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setBitmap(bitmap, null, true, which)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun triggerHapticFeedback(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(45)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration is not supported
        }
    }
}
