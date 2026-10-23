package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Color Scheme matching https://numberplate.selldibo.com/
val SelldiboLightColorScheme = lightColorScheme(
    primary = SelldiboGreen,
    onPrimary = Color.White,
    primaryContainer = SelldiboGreenSoft,
    onPrimaryContainer = SelldiboGreenDark,
    secondary = SelldiboGreenDark,
    onSecondary = Color.White,
    secondaryContainer = SelldiboGreenSoft,
    onSecondaryContainer = SelldiboGreenDark,
    tertiary = SelldiboRed,
    onTertiary = Color.White,
    tertiaryContainer = SelldiboRedSoft,
    onTertiaryContainer = Color(0xFF991B1B),
    background = SelldiboBg,
    onBackground = SelldiboInk,
    surface = SelldiboSurface,
    onSurface = SelldiboInk,
    surfaceVariant = SelldiboBgAlt,
    onSurfaceVariant = SelldiboMuted,
    outline = SelldiboLine,
    outlineVariant = SelldiboInputLine
)

/**
 * PlateCraft Theme: Strict Light Theme only per explicit user requirement.
 * Faithful to SellDibo Number Plate Studio (Bangladesh Deep Green #006A4E,
 * Soft Sage #E8F3EF, Crisp #FFFFFF Cards, and #1D2B26 Ink).
 */
@Composable
fun PlateCraftTheme(
    darkTheme: Boolean = false, // Enforced Light Theme Only
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SelldiboLightColorScheme,
        typography = AppTypography,
        content = content
    )
}
