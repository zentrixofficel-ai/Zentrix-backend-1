package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZentrixDarkColorScheme = darkColorScheme(
    primary = ZentrixCyan,
    onPrimary = Color(0xFF041E28),
    primaryContainer = Color(0xFF003643),
    onPrimaryContainer = Color(0xFF86F3FF),
    secondary = ZentrixPurple,
    onSecondary = Color(0xFF24005A),
    secondaryContainer = Color(0xFF3B1578),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = ZentrixAmber,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF633F00),
    onTertiaryContainer = Color(0xFFFFDDB3),
    background = ConsoleBackground,
    onBackground = TextPrimary,
    surface = ConsoleSurface,
    onSurface = TextPrimary,
    surfaceVariant = ConsoleSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ConsoleCardBorder,
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve sleek custom cyber cloud palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZentrixDarkColorScheme,
        typography = Typography,
        content = content
    )
}
