package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FullscreenParallaxScreen
import com.example.ui.components.MainWallpaperScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: WallpaperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val favorites by viewModel.favorites.collectAsStateWithLifecycle()
                val parallaxOffset by viewModel.parallaxOffset.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    viewModel.userEvents.collectLatest { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainWallpaperScreen(
                        catalog = viewModel.catalog,
                        favorites = favorites,
                        selectedCategory = uiState.selectedCategory,
                        selectedWallpaper = uiState.selectedWallpaper,
                        parallaxOffset = parallaxOffset,
                        parallaxIntensity = uiState.parallaxIntensity,
                        particlesEnabled = uiState.particlesEnabled,
                        gyroActive = uiState.gyroSensorActive,
                        snackbarHostState = snackbarHostState,
                        onSelectCategory = { viewModel.selectCategory(it) },
                        onSelectWallpaper = { viewModel.selectWallpaper(it) },
                        onOpenFullscreen = { viewModel.openFullscreenPreview(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onIntensityChange = { viewModel.setParallaxIntensity(it) },
                        onToggleParticles = { viewModel.toggleParticles() },
                        onToggleGyro = { viewModel.toggleGyroSensor() }
                    )

                    AnimatedVisibility(
                        visible = uiState.isFullscreenPreview && uiState.selectedWallpaper != null,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {
                        uiState.selectedWallpaper?.let { currentWallpaper ->
                            val isFav = favorites.any { it.id == currentWallpaper.id }
                            FullscreenParallaxScreen(
                                wallpaper = currentWallpaper,
                                sensorRoll = parallaxOffset.roll,
                                sensorPitch = parallaxOffset.pitch,
                                intensity = uiState.parallaxIntensity,
                                particlesEnabled = uiState.particlesEnabled,
                                gyroActive = uiState.gyroSensorActive,
                                isFavorite = isFav,
                                isApplying = uiState.isApplyingWallpaper,
                                onBack = { viewModel.closeFullscreenPreview() },
                                onIntensityChange = { viewModel.setParallaxIntensity(it) },
                                onToggleParticles = { viewModel.toggleParticles() },
                                onToggleGyro = { viewModel.toggleGyroSensor() },
                                onToggleFavorite = { viewModel.toggleFavorite(currentWallpaper) },
                                onApplyWallpaper = { target ->
                                    viewModel.applyWallpaperToPhone(currentWallpaper, target)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
