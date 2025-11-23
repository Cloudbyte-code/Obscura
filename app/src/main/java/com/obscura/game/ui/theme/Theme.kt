package com.obscura.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ObscuraDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7B1FA2),
    secondary = Color(0xFF512DA8),
    tertiary = Color(0xFFAA00FF),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
)

@Composable
fun ObscuraTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ObscuraDarkColorScheme,
        content = content
    )
}
