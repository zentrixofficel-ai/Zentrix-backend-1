package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FirebaseLightColorScheme = lightColorScheme(
    primary = ButtonBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F0FE),
    onPrimaryContainer = ButtonBlue,
    secondary = ButtonYellow,
    onSecondary = Color(0xFF202124),
    secondaryContainer = Color(0xFFFEF7E0),
    onSecondaryContainer = Color(0xFF5F6368),
    tertiary = ButtonGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6F4EA),
    onTertiaryContainer = ButtonGreen,
    background = FirebaseBackground,
    onBackground = TextPrimary,
    surface = FirebaseSurface,
    onSurface = TextPrimary,
    surfaceVariant = FirebaseSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = FirebaseCardBorder,
    outlineVariant = Color(0xFFE8EAED)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Pure crisp white Firebase theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FirebaseLightColorScheme,
        typography = Typography,
        content = content
    )
}
