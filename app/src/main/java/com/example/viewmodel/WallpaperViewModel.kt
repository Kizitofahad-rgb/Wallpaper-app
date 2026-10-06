package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.FavoriteWallpaper
import com.example.data.WallpaperDatabase
import com.example.data.WallpaperRepository
import com.example.model.WallpaperCategory
import com.example.model.WallpaperItem
import com.example.sensor.GyroscopeManager
import com.example.sensor.ParallaxOffset
import com.example.util.WallpaperHelper
import com.example.util.WallpaperTarget
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WallpaperUiState(
    val selectedCategory: WallpaperCategory = WallpaperCategory.ALL,
    val selectedWallpaper: WallpaperItem? = null,
    val isFullscreenPreview: Boolean = false,
    val parallaxIntensity: Float = 1.0f,
    val particlesEnabled: Boolean = true,
    val gyroSensorActive: Boolean = true,
    val isApplyingWallpaper: Boolean = false,
    val applySuccessMessage: String? = null
)

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        WallpaperDatabase::class.java,
        "omni_anime_wallpapers.db"
    ).fallbackToDestructiveMigration().build()

    private val repository = WallpaperRepository(db.favoriteDao())
    val gyroscopeManager = GyroscopeManager(application)

    val catalog: List<WallpaperItem> = repository.catalog

    val favorites: StateFlow<List<FavoriteWallpaper>> = repository.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val parallaxOffset: StateFlow<ParallaxOffset> = gyroscopeManager.parallaxOffset

    private val _uiState = MutableStateFlow(
        WallpaperUiState(selectedWallpaper = repository.catalog.firstOrNull())
    )
    val uiState: StateFlow<WallpaperUiState> = _uiState.asStateFlow()

    private val _userEvents = MutableSharedFlow<String>()
    val userEvents: SharedFlow<String> = _userEvents.asSharedFlow()

    init {
        gyroscopeManager.startListening()
    }

    override fun onCleared() {
        super.onCleared()
        gyroscopeManager.stopListening()
    }

    fun selectCategory(category: WallpaperCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun selectWallpaper(wallpaper: WallpaperItem) {
        _uiState.value = _uiState.value.copy(selectedWallpaper = wallpaper)
    }

    fun openFullscreenPreview(wallpaper: WallpaperItem) {
        _uiState.value = _uiState.value.copy(
            selectedWallpaper = wallpaper,
            isFullscreenPreview = true
        )
    }

    fun closeFullscreenPreview() {
        _uiState.value = _uiState.value.copy(isFullscreenPreview = false)
    }

    fun setParallaxIntensity(intensity: Float) {
        _uiState.value = _uiState.value.copy(parallaxIntensity = intensity)
    }

    fun toggleParticles() {
        _uiState.value = _uiState.value.copy(particlesEnabled = !_uiState.value.particlesEnabled)
    }

    fun toggleGyroSensor() {
        val next = !_uiState.value.gyroSensorActive
        _uiState.value = _uiState.value.copy(gyroSensorActive = next)
        if (next) {
            gyroscopeManager.startListening()
        } else {
            gyroscopeManager.stopListening()
        }
    }

    fun toggleFavorite(wallpaper: WallpaperItem) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.id == wallpaper.id }
            repository.toggleFavorite(wallpaper, isFav)
            WallpaperHelper.triggerHapticFeedback(getApplication())
            _userEvents.emit(if (isFav) "Removed from favorites" else "Saved to favorites!")
        }
    }

    fun applyWallpaperToPhone(wallpaper: WallpaperItem, target: WallpaperTarget) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isApplyingWallpaper = true)
            val result = WallpaperHelper.applyWallpaper(getApplication(), wallpaper.resId, target)
            _uiState.value = _uiState.value.copy(isApplyingWallpaper = false)

            result.onSuccess {
                WallpaperHelper.triggerHapticFeedback(getApplication())
                val label = when (target) {
                    WallpaperTarget.HOME -> "Home Screen"
                    WallpaperTarget.LOCK -> "Lock Screen"
                    WallpaperTarget.BOTH -> "Home & Lock Screen"
                }
                _userEvents.emit("Wallpaper successfully set to $label!")
            }.onFailure { err ->
                _userEvents.emit("Failed to set wallpaper: ${err.localizedMessage}")
            }
        }
    }
}
