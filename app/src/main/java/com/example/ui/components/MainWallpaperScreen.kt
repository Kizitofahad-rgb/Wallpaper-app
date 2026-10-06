package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoriteWallpaper
import com.example.model.WallpaperCategory
import com.example.model.WallpaperItem
import com.example.sensor.ParallaxOffset

enum class MainTab(val title: String) {
    GALLERY("Gallery"),
    FAVORITES("Vault"),
    CALIBRATION("Physics")
}

@Composable
fun MainWallpaperScreen(
    catalog: List<WallpaperItem>,
    favorites: List<FavoriteWallpaper>,
    selectedCategory: WallpaperCategory,
    selectedWallpaper: WallpaperItem?,
    parallaxOffset: ParallaxOffset,
    parallaxIntensity: Float,
    particlesEnabled: Boolean,
    gyroActive: Boolean,
    snackbarHostState: SnackbarHostState,
    onSelectCategory: (WallpaperCategory) -> Unit,
    onSelectWallpaper: (WallpaperItem) -> Unit,
    onOpenFullscreen: (WallpaperItem) -> Unit,
    onToggleFavorite: (WallpaperItem) -> Unit,
    onIntensityChange: (Float) -> Unit,
    onToggleParticles: () -> Unit,
    onToggleGyro: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(MainTab.GALLERY) }

    val filteredList = remember(selectedCategory, catalog, favorites, activeTab) {
        when (activeTab) {
            MainTab.GALLERY -> {
                if (selectedCategory == WallpaperCategory.ALL) catalog
                else catalog.filter { it.category == selectedCategory }
            }
            MainTab.FAVORITES -> {
                val favIds = favorites.map { it.id }.toSet()
                catalog.filter { favIds.contains(it.id) }
            }
            MainTab.CALIBRATION -> emptyList()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_wallpaper_scaffold"),
        containerColor = Color(0xFF07090E),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0A0E17),
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == MainTab.GALLERY,
                    onClick = { activeTab = MainTab.GALLERY },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Gallery") },
                    label = { Text("4K Universe") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF07090E),
                        indicatorColor = Color(0xFF00F0FF),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8),
                        selectedTextColor = Color(0xFF00F0FF)
                    ),
                    modifier = Modifier.testTag("nav_item_gallery")
                )

                NavigationBarItem(
                    selected = activeTab == MainTab.FAVORITES,
                    onClick = { activeTab = MainTab.FAVORITES },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Favorites") },
                    label = { Text("Vault (${favorites.size})") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF07090E),
                        indicatorColor = Color(0xFFFF2A6D),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8),
                        selectedTextColor = Color(0xFFFF2A6D)
                    ),
                    modifier = Modifier.testTag("nav_item_favorites")
                )

                NavigationBarItem(
                    selected = activeTab == MainTab.CALIBRATION,
                    onClick = { activeTab = MainTab.CALIBRATION },
                    icon = { Icon(Icons.Default.Sensors, contentDescription = "Calibration") },
                    label = { Text("3D Physics") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF07090E),
                        indicatorColor = Color(0xFFBD00FF),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8),
                        selectedTextColor = Color(0xFFBD00FF)
                    ),
                    modifier = Modifier.testTag("nav_item_calibration")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OMNI ANIME 4K",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Hyper-Realistic 3D Depth Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00F0FF)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF131B2A),
                    border = BorderStroke(1.dp, Color(0xFF28354D))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (gyroActive) Color(0xFF00F0FF) else Color.Gray,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "120 FPS PARALLAX",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            when (activeTab) {
                MainTab.GALLERY, MainTab.FAVORITES -> {
                    // Category Chips (for Gallery)
                    if (activeTab == MainTab.GALLERY) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WallpaperCategory.entries.forEach { category ->
                                val selected = category == selectedCategory
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectCategory(category) },
                                    label = { Text(category.title) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color(0xFF0F141F),
                                        labelColor = Color(0xFF94A3B8),
                                        selectedContainerColor = Color(0xFF00F0FF).copy(alpha = 0.2f),
                                        selectedLabelColor = Color(0xFF00F0FF)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        borderColor = if (selected) Color(0xFF00F0FF) else Color(0xFF28354D)
                                    ),
                                    modifier = Modifier.testTag("chip_category_${category.name}")
                                )
                            }
                        }
                    }

                    // Live Miniature Interactive Hero Parallax Box
                    selectedWallpaper?.let { hero ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(hero.accentColorHex).copy(alpha = 0.8f),
                                            Color(0xFFBD00FF).copy(alpha = 0.5f)
                                        )
                                    ),
                                    RoundedCornerShape(22.dp)
                                )
                                .clickable { onOpenFullscreen(hero) }
                                .testTag("hero_preview_banner")
                        ) {
                            HyperParallaxViewer(
                                wallpaper = hero,
                                sensorRoll = if (gyroActive) parallaxOffset.roll else 0f,
                                sensorPitch = if (gyroActive) parallaxOffset.pitch else 0f,
                                intensity = parallaxIntensity,
                                touchInteractive = true,
                                showParticles = particlesEnabled,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay info on hero banner
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xD007090E))
                                        )
                                    )
                                    .padding(14.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(hero.accentColorHex)
                                        ) {
                                            Text(
                                                text = "ACTIVE 3D POP-OUT",
                                                color = Color(0xFF07090E),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = hero.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Tilt your device to see holographic depth layer shift",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }

                                    Button(
                                        onClick = { onOpenFullscreen(hero) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(hero.accentColorHex)
                                        ),
                                        modifier = Modifier.testTag("btn_enter_fullscreen")
                                    ) {
                                        Icon(
                                            Icons.Default.OpenInFull,
                                            contentDescription = null,
                                            tint = Color(0xFF07090E),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "IMMERSION",
                                            color = Color(0xFF07090E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Wallpapers Grid
                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Your Vault is Empty",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the heart icon on any wallpaper to store your favorites here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("wallpaper_grid")
                        ) {
                            items(filteredList, key = { it.id }) { item ->
                                val isFav = favorites.any { it.id == item.id }
                                WallpaperCard(
                                    wallpaper = item,
                                    isFavorite = isFav,
                                    onSelect = { onSelectWallpaper(item) },
                                    onPreview = { onOpenFullscreen(item) },
                                    onToggleFavorite = { onToggleFavorite(item) }
                                )
                            }
                        }
                    }
                }

                MainTab.CALIBRATION -> {
                    // Physics & Sensor Calibration Pane
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Real-Time Sensor Telemetry",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Direct hardware gyroscope & accelerometer readings delivering 120 FPS depth translation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141F)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF28354D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "GYRO ANGLES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Roll (X-Axis):", color = Color.White)
                                    Text(
                                        String.format("%.2f rad", parallaxOffset.roll),
                                        color = Color(0xFF00F0FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pitch (Y-Axis):", color = Color.White)
                                    Text(
                                        String.format("%.2f rad", parallaxOffset.pitch),
                                        color = Color(0xFFBD00FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Global Depth Calibration: ${(parallaxIntensity * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Slider(
                            value = parallaxIntensity,
                            onValueChange = onIntensityChange,
                            valueRange = 0.3f..2.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00F0FF),
                                activeTrackColor = Color(0xFF00F0FF)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onToggleGyro,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gyroActive) Color(0xFF192132) else Color(0xFF00F0FF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (gyroActive) "PAUSE GYRO SENSOR" else "RESUME GYRO SENSOR",
                                color = if (gyroActive) Color.White else Color(0xFF07090E),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onToggleParticles,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (particlesEnabled) Color(0xFF192132) else Color(0xFFBD00FF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MotionPhotosOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (particlesEnabled) "DISABLE FLOATING SPARKS" else "ENABLE FLOATING SPARKS",
                                color = if (particlesEnabled) Color.White else Color(0xFF07090E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
