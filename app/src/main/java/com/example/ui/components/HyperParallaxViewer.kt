package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.WallpaperItem
import kotlin.math.roundToInt

@Composable
fun HyperParallaxViewer(
    wallpaper: WallpaperItem,
    sensorRoll: Float,
    sensorPitch: Float,
    intensity: Float = 1.0f,
    touchInteractive: Boolean = true,
    showParticles: Boolean = true,
    modifier: Modifier = Modifier
) {
    var touchRollOffset by remember { mutableFloatStateOf(0f) }
    var touchPitchOffset by remember { mutableFloatStateOf(0f) }

    val combinedRoll = (sensorRoll + touchRollOffset).coerceIn(-1.5f, 1.5f) * intensity
    val combinedPitch = (sensorPitch + touchPitchOffset).coerceIn(-1.5f, 1.5f) * intensity

    val smoothRoll by animateFloatAsState(
        targetValue = combinedRoll,
        animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing),
        label = "smoothRoll"
    )
    val smoothPitch by animateFloatAsState(
        targetValue = combinedPitch,
        animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing),
        label = "smoothPitch"
    )

    val maxDisplacementPx = 65f * intensity
    val maxRotationDeg = 8f * intensity

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04060A))
            .testTag("parallax_viewer_container")
            .then(
                if (touchInteractive) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                touchRollOffset = (touchRollOffset + dragAmount.x / 400f).coerceIn(-0.8f, 0.8f)
                                touchPitchOffset = (touchPitchOffset + dragAmount.y / 400f).coerceIn(-0.8f, 0.8f)
                            },
                            onDragEnd = {
                                touchRollOffset = 0f
                                touchPitchOffset = 0f
                            },
                            onDragCancel = {
                                touchRollOffset = 0f
                                touchPitchOffset = 0f
                            }
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Deep Cosmic / Abyss Background (Far Plane)
        // Moves oppositely with subtle scale and blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.22f)
                .offset {
                    IntOffset(
                        x = (-smoothRoll * maxDisplacementPx * 0.25f).roundToInt(),
                        y = (-smoothPitch * maxDisplacementPx * 0.25f).roundToInt()
                    )
                }
                .blur(radius = 12.dp)
                .alpha(0.55f)
        ) {
            Image(
                painter = painterResource(id = wallpaper.resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Vignette dark aura
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0xCC05080E)),
                            radius = 900f
                        )
                    )
            )
        }

        // LAYER 2: Midground Atmosphere & Core Canvas
        // Rotates subtly in 3D perspective space (RotationX and RotationY)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.15f)
                .graphicsLayer {
                    rotationY = smoothRoll * maxRotationDeg
                    rotationX = -smoothPitch * maxRotationDeg
                    cameraDistance = 12f * density
                    translationX = smoothRoll * maxDisplacementPx * 0.55f
                    translationY = smoothPitch * maxDisplacementPx * 0.55f
                }
        ) {
            Image(
                painter = painterResource(id = wallpaper.resId),
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // LAYER 3: Out-of-Screen Pop-Out Foreground Aura
        // Shifts heavily forward with camera perspective scaling
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.25f)
                .graphicsLayer {
                    rotationY = smoothRoll * (maxRotationDeg * 1.35f)
                    rotationX = -smoothPitch * (maxRotationDeg * 1.35f)
                    translationX = smoothRoll * maxDisplacementPx * 1.25f
                    translationY = smoothPitch * maxDisplacementPx * 1.25f
                    cameraDistance = 8f * density
                }
        ) {
            // Ethereal rim-light overlay matching anime character's energy aura
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(wallpaper.accentColorHex).copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            radius = 650f
                        )
                    )
            )
        }

        // LAYER 4: Dimensional Particles Floating Directly Over the Screen Glass
        if (showParticles) {
            DepthParticleField(
                particleType = wallpaper.particleEffect,
                accentColor = Color(wallpaper.accentColorHex),
                roll = smoothRoll,
                pitch = smoothPitch,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
