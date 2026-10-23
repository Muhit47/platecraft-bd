package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Color Scheme matching official Bangladesh plate palette
val PlateCraftLightColorScheme = lightColorScheme(
    primary = PlateCraftGreen,
    onPrimary = Color.White,
    primaryContainer = PlateCraftGreenSoft,
    onPrimaryContainer = PlateCraftGreenDark,
    secondary = PlateCraftGreenDark,
    onSecondary = Color.White,
    secondaryContainer = PlateCraftGreenSoft,
    onSecondaryContainer = PlateCraftGreenDark,
    tertiary = PlateCraftRed,
    onTertiary = Color.White,
    tertiaryContainer = PlateCraftRedSoft,
    onTertiaryContainer = Color(0xFF991B1B),
    background = PlateCraftBg,
    onBackground = PlateCraftInk,
    surface = PlateCraftSurface,
    onSurface = PlateCraftInk,
    surfaceVariant = PlateCraftBgAlt,
    onSurfaceVariant = PlateCraftMuted,
    outline = PlateCraftLine,
    outlineVariant = PlateCraftInputLine
)

/**
 * PlateCraft Theme: Strict Light Theme only per explicit user requirement.
 * BRTA vehicle plate design studio (Bangladesh Deep Green #006A4E,
 * Soft Mint #E8F3EF, Crisp #FFFFFF Cards, and #1D2B26 Ink).
 */
@Composable
fun PlateCraftTheme(
    darkTheme: Boolean = false, // Enforced Light Theme Only
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = PlateCraftLightColorScheme,
        typography = AppTypography,
        content = content
    )
}
