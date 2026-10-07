package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZentrixDarkColorScheme = darkColorScheme(
    primary = ZentrixCyan,
    onPrimary = Color(0xFF090D16),
    primaryContainer = Color(0xFF0E2A3B),
    onPrimaryContainer = ZentrixCyan,
    secondary = ZentrixViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF261E42),
    onSecondaryContainer = ZentrixViolet,
    tertiary = ZentrixGreen,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF103324),
    onTertiaryContainer = ZentrixGreen,
    background = ZentrixBackground,
    onBackground = TextPrimary,
    surface = ZentrixSurface,
    onSurface = TextPrimary,
    surfaceVariant = ZentrixSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ZentrixCardBorder,
    outlineVariant = Color(0xFF1B283E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZentrixDarkColorScheme,
        typography = Typography,
        content = content
    )
}
