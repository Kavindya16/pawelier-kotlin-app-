package com.example.pawelierapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFAB91),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFFFFAB91),
    onPrimaryContainer = Color(0xFFFFF8F5),
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF81D4FA),
    onSecondaryContainer = Color(0xFFFFF8F5),
    tertiary = Color(0xFFFFE082),
    onTertiary = Color(0xFF1A1A1A),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFFFF8F5),
    surface = Color(0xFF2D2D2D),
    onSurface = Color(0xFFFFF8F5),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFFFF8F5),
    error = Color(0xFFEF5350),
    onError = Color(0xFFFFF8F5)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = Color(0xFFFFF8F5),
    primaryContainer = Color(0xFFFF8A65),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = Color(0xFF4FC3F7),
    onSecondary = Color(0xFFFFF8F5),
    secondaryContainer = Color(0xFF4FC3F7),
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFFFFF8F5),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF1A1A1A),
    error = Color(0xFFEF5350),
    onError = Color(0xFFFFF8F5)
)

@Composable
fun PawelierAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colors.background
            ) {
                content()
            }
        }
    )
}