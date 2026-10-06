package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AnimeDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = VoidDark,
    primaryContainer = VoidSurfaceVariant,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = TextPrimary,
    secondaryContainer = VoidSurfaceVariant,
    onSecondaryContainer = NeonPurple,
    tertiary = NeonCrimson,
    onTertiary = TextPrimary,
    background = VoidDark,
    onBackground = TextPrimary,
    surface = VoidSurface,
    onSurface = TextPrimary,
    surfaceVariant = VoidSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = VoidBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AnimeDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
