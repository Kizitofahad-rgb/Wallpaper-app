package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WallpaperItem
import com.example.util.WallpaperTarget

@Composable
fun FullscreenParallaxScreen(
    wallpaper: WallpaperItem,
    sensorRoll: Float,
    sensorPitch: Float,
    intensity: Float,
    particlesEnabled: Boolean,
    gyroActive: Boolean,
    isFavorite: Boolean,
    isApplying: Boolean,
    onBack: () -> Unit,
    onIntensityChange: (Float) -> Unit,
    onToggleParticles: () -> Unit,
    onToggleGyro: () -> Unit,
    onToggleFavorite: () -> Unit,
    onApplyWallpaper: (WallpaperTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    var showControls by remember { mutableStateOf(true) }
    var showTuningSheet by remember { mutableStateOf(false) }
    var showApplyMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("fullscreen_preview_screen")
    ) {
        // Multi-Layer High-Frame-Rate Parallax Canvas
        HyperParallaxViewer(
            wallpaper = wallpaper,
            sensorRoll = if (gyroActive) sensorRoll else 0f,
            sensorPitch = if (gyroActive) sensorPitch else 0f,
            intensity = intensity,
            touchInteractive = true,
            showParticles = particlesEnabled,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Tap to Hide/Show HUD controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xCC07090E),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.size(46.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("btn_back_preview")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Gyro Active Indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xCC07090E),
                            border = BorderStroke(
                                1.dp,
                                if (gyroActive) Color(wallpaper.accentColorHex) else Color.Gray
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = "Gyro status",
                                    tint = if (gyroActive) Color(wallpaper.accentColorHex) else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (gyroActive) "Tilt Phone" else "Gyro Off",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Tuning Menu
                        Surface(
                            shape = CircleShape,
                            color = Color(0xCC07090E),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .size(46.dp)
                                .padding(end = 6.dp)
                        ) {
                            IconButton(
                                onClick = { showTuningSheet = !showTuningSheet },
                                modifier = Modifier.testTag("btn_tune_depth")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Tune 3D Parallax Depth",
                                    tint = Color.White
                                )
                            }
                        }

                        // Favorite toggle
                        Surface(
                            shape = CircleShape,
                            color = Color(0xCC07090E),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.size(46.dp)
                        ) {
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier.testTag("btn_fav_fullscreen")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle favorite",
                                    tint = if (isFavorite) Color(0xFFFF2A6D) else Color.White
                                )
                            }
                        }
                    }
                }

                // Tuning Panel overlay
                if (showTuningSheet) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 80.dp, end = 16.dp)
                            .width(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xF20F141F),
                        border = BorderStroke(1.dp, Color(wallpaper.accentColorHex).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Depth & Physics Tuning",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Parallax Intensity: ${(intensity * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                            Slider(
                                value = intensity,
                                onValueChange = onIntensityChange,
                                valueRange = 0.3f..2.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(wallpaper.accentColorHex),
                                    activeTrackColor = Color(wallpaper.accentColorHex)
                                ),
                                modifier = Modifier.testTag("slider_intensity")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3D Floating Sparks",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Switch(
                                    checked = particlesEnabled,
                                    onCheckedChange = { onToggleParticles() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(wallpaper.accentColorHex),
                                        checkedTrackColor = Color(wallpaper.accentColorHex).copy(alpha = 0.4f)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gyroscope Hardware",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Switch(
                                    checked = gyroActive,
                                    onCheckedChange = { onToggleGyro() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(wallpaper.accentColorHex),
                                        checkedTrackColor = Color(wallpaper.accentColorHex).copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
                }

                // Bottom HUD: Wallpaper Info + Apply Button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xF507090E))
                            )
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = wallpaper.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = wallpaper.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF192132),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "POP-OUT DEPTH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = wallpaper.depthRating,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(wallpaper.accentColorHex),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF192132),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "RESOLUTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Ultra 4K UHD",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Apply Button
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showApplyMenu = true },
                            enabled = !isApplying,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(wallpaper.accentColorHex)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_apply_wallpaper")
                        ) {
                            if (isApplying) {
                                CircularProgressIndicator(
                                    color = Color(0xFF07090E),
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Applying 4K Wallpaper...",
                                    color = Color(0xFF07090E),
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.FormatPaint,
                                    contentDescription = null,
                                    tint = Color(0xFF07090E)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SET AS 4K WALLPAPER",
                                    color = Color(0xFF07090E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showApplyMenu,
                            onDismissRequest = { showApplyMenu = false },
                            modifier = Modifier.background(Color(0xFF0F141F))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Home Screen", color = Color.White) },
                                onClick = {
                                    showApplyMenu = false
                                    onApplyWallpaper(WallpaperTarget.HOME)
                                },
                                modifier = Modifier.testTag("menu_apply_home")
                            )
                            DropdownMenuItem(
                                text = { Text("Lock Screen", color = Color.White) },
                                onClick = {
                                    showApplyMenu = false
                                    onApplyWallpaper(WallpaperTarget.LOCK)
                                },
                                modifier = Modifier.testTag("menu_apply_lock")
                            )
                            DropdownMenuItem(
                                text = { Text("Both (Home & Lock Screen)", color = Color(wallpaper.accentColorHex)) },
                                onClick = {
                                    showApplyMenu = false
                                    onApplyWallpaper(WallpaperTarget.BOTH)
                                },
                                modifier = Modifier.testTag("menu_apply_both")
                            )
                        }
                    }
                }
            }
        }
    }
}
